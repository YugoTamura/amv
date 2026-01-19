package dev.aulait.amv.domain.extractor.java;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import dev.aulait.amv.domain.extractor.fdo.FieldFdo;
import dev.aulait.amv.domain.extractor.fdo.FlowStatementFdo;
import dev.aulait.amv.domain.extractor.fdo.MethodCallFdo;
import dev.aulait.amv.domain.extractor.fdo.MethodFdo;
import dev.aulait.amv.domain.extractor.fdo.TypeFdo;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

@Builder
@Slf4j
public class AmvVisitor extends VoidVisitorAdapter<AmvVisitorContext> {

  @Builder.Default private MetadataConverter converter = new MetadataConverter();
  @Builder.Default private List<MetadataAdjuster> adjusters = new ArrayList<>();

  @Override
  public void visit(ClassOrInterfaceDeclaration n, AmvVisitorContext context) {
    TypeFdo type = converter.convert(n);

    context.pushType(type);
    super.visit(n, context);

    adjusters.forEach(adjuster -> adjuster.adjust(type));

    context.popType();
  }

  @Override
  public void visit(EnumDeclaration n, AmvVisitorContext context) {
    TypeFdo type = converter.convert(n);

    context.pushType(type);
    super.visit(n, context);
    context.popType();
  }

  @Override
  public void visit(FieldDeclaration n, AmvVisitorContext context) {
    FieldFdo field = converter.convert(n);
    context.getCurrentType().getFields().add(field);
    super.visit(n, context);
  }

  @Override
  public void visit(MethodDeclaration n, AmvVisitorContext context) {
    MethodFdo method = converter.convert(n);
    context.getCurrentType().getMethods().add(method);

    context.pushMethod(method);
    super.visit(n, context);
    context.popMethod();
  }

  @Override
  public void visit(MethodCallExpr n, AmvVisitorContext context) {
    MethodCallFdo methodCall = converter.convert(n);

    MethodFdo currentMethod = context.getCurrentMethod();
    // currentMethod is null if it's called from inside a static constructor.
    if (currentMethod != null) {
      currentMethod.getMethodCalls().add(methodCall);
    }

    methodCall.setId(UUID.randomUUID().toString());
    MethodCallFdo currentMethodCall = context.getCurrentMethodCall();
    if (currentMethodCall != null) {
      methodCall.setCallerId(currentMethodCall.getId());
    }

    context.pushMethodCall(methodCall);
    super.visit(n, context);
    context.popMethodCall();
  }

  @Override
  public void visit(MethodReferenceExpr n, AmvVisitorContext context) {
    MethodCallFdo methodCall = converter.convert(n);

    MethodFdo currentMethod = context.getCurrentMethod();
    // currentMethod is null if it's called from inside a static constructor.
    if (currentMethod != null) {
      currentMethod.getMethodCalls().add(methodCall);
    }

    super.visit(n, context);
  }

  @Override
  public void visit(IfStmt n, AmvVisitorContext context) {
    handleFlow(
        n,
        converter.convert(n),
        context,
        (node, ctx) -> {
          node.getCondition().accept(this, ctx);
          node.getThenStmt().accept(this, ctx);

          node.getElseStmt()
              .ifPresent(
                  elseStmt -> {
                    if (elseStmt.isIfStmt()) {
                      elseStmt.asIfStmt().accept(this, ctx);
                    } else {
                      FlowStatementFdo elseFlow = converter.convert(elseStmt);
                      handleFlow(elseStmt, elseFlow, ctx, (s, c) -> s.accept(this, c));
                    }
                  });
        });
  }

  @Override
  public void visit(ForStmt n, AmvVisitorContext context) {
    handleFlow(n, converter.convert(n), context, (node, ctx) -> super.visit(node, ctx));
  }

  @Override
  public void visit(ForEachStmt n, AmvVisitorContext context) {
    handleFlow(n, converter.convert(n), context, (node, ctx) -> super.visit(node, ctx));
  }

  @Override
  public void visit(ReturnStmt n, AmvVisitorContext context) {
    handleFlow(n, converter.convert(n), context, (node, ctx) -> super.visit(node, ctx));
  }

  @Override
  public void visit(TryStmt n, AmvVisitorContext context) {
    handleFlow(
        n,
        converter.convert(n),
        context,
        (node, ctx) -> {
          n.getTryBlock().accept(this, ctx);

          for (CatchClause cc : n.getCatchClauses()) {
            handleFlow(cc, converter.convert(cc), ctx, (cnode, cctx) -> super.visit(cnode, cctx));
          }

          node.getFinallyBlock()
              .ifPresent(
                  fb -> {
                    FlowStatementFdo finallyFlow = converter.convert(fb);
                    handleFlow(fb, finallyFlow, ctx, (s, c) -> super.visit(s, c));
                  });
        });
  }

  private <T extends Node> void handleFlow(
      T n,
      FlowStatementFdo flowStatement,
      AmvVisitorContext context,
      BiConsumer<T, AmvVisitorContext> traverse) {

    FlowStatementFdo parent = context.getCurrentFlowStatement();
    if (parent != null) {
      flowStatement.setParent(parent);
    }

    MethodFdo methodF = context.getCurrentMethod();
    if (methodF == null) {
      return;
    }

    MethodCallFdo methodCallF = context.getCurrentMethodCall();
    if (methodCallF != null) {
      methodCallF.setFlowStatement(flowStatement);
    } else {
      if (methodF.getFlowStatements() == null) {
        methodF.setFlowStatements(new ArrayList<>());
      }
      methodF.getFlowStatements().add(flowStatement);
    }

    context.pushFlowStatement(flowStatement);
    traverse.accept(n, context);
    context.popFlowStatement();
  }
}
