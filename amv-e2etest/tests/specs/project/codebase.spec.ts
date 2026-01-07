import { test } from '@playwright/test';
import { DryRun } from '@arch/DryRun';
import { locale } from '@arch/MultiLng';
import TopPage from '@pages/top/TopPage';
import CodebaseInputFactory from '@factories/CodebaseFactory';

// TODO: Improve parallel execution time
test('Analyze a repository', async ({ browser, browserName }) => {
  if (browserName !== 'chromium') await new Promise((r) => setTimeout(r, 15000));
  const context = await browser.newContext({
    locale: locale
  });

  const page = await context.newPage();
  const dryRun = DryRun.build();

  const topPage = new TopPage(page, dryRun);
  const codebase = CodebaseInputFactory.createTestCodebase();
  const { page: codebaseInputPage, isVisible } = await topPage.gotoNewCodebasePage(codebase);

  if (!isVisible) {
    // Create
    await codebaseInputPage.create(codebase);
    await codebaseInputPage.expectSavedSuccessfully();

    // Analyze
    await codebaseInputPage.analyze();
  }

  await codebaseInputPage.expectCodebase(codebase);
});
