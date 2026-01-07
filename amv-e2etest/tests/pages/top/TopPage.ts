import { Page } from '@playwright/test';
import TopPageElement from '@pages/top/TopPageElement';
import { DryRun } from '@arch/DryRun';
import MenuBar from '@pages/menu-bar/MenuBar';
import CodebaseInputPage from '@pages/codebase-input/CodebaseInputPage';
import type { CodebaseModel } from '@api/Api';

export default class TopPage {
  private readonly topPageEl: TopPageElement;

  constructor(page: Page, dryRun: DryRun) {
    this.topPageEl = new TopPageElement({ page, dryRun });
  }

  async open() {
    await this.topPageEl.open();
    return new MenuBar(this.topPageEl);
  }

  async gotoNewCodebasePage(codebase: CodebaseModel) {
    await this.topPageEl.open();
    const isVisible = await this.topPageEl.isLinkVisibleByName(codebase.name);
    isVisible
      ? await this.topPageEl.clickCodebaseIdLink(codebase.name)
      : await this.topPageEl.clickNewCodebaseLink();
    return {
      page: new CodebaseInputPage(this.topPageEl),
      isVisible
    };
  }
}
