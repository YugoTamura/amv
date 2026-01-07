import CodebaseInputPageElement from '@pages/codebase-input/CodebaseInputPageElement';
import type { CodebaseModel } from '@api/Api';
import BasePageElement from '@arch/BasePageElement';

export default class CodebaseInputPage {
  private readonly codebaseInputPageEl: CodebaseInputPageElement;

  constructor(page: BasePageElement) {
    this.codebaseInputPageEl = new CodebaseInputPageElement(page);
  }

  async create(codebase: CodebaseModel) {
    await this.codebaseInputPageEl.inputUrl(codebase.url);

    await this.codebaseInputPageEl.clickSaveBtn();
  }

  async analyze() {
    await this.codebaseInputPageEl.clickAnalyzeBtn();
  }

  async expectSavedSuccessfully() {
    await this.codebaseInputPageEl.expectSavedSuccessfully();
  }

  async expectAnalyzedSuccessfully() {
    await this.codebaseInputPageEl.expectAnalyzing();
    await this.codebaseInputPageEl.expectAnalyzedSuccessfully();
  }

  async expectCodebase(codebase: CodebaseModel) {
    await this.codebaseInputPageEl.expectProjects(codebase.projects);
  }
}
