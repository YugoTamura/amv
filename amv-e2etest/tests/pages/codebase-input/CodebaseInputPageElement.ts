import BasePageElement from '@arch/BasePageElement';
import * as m from '@paraglide/messages';
import type { ProjectModel } from '@api/Api';

export default class CodebaseInputPageElement extends BasePageElement {
  get pageNameKey() {
    return 'newCodebase';
  }

  async inputUrl(url?: string) {
    await this.inputText('#url', url ?? '');
  }

  async expectProjects(projects?: ProjectModel[]) {
    await this.expectTextExist('#project > span', projects?.map((p) => p.name ?? '') ?? []);
  }

  async clickSaveBtn() {
    await this.click('#save');
  }

  async expectSavedSuccessfully() {
    await this.expectGlobalMessage(m.saved());
  }

  async clickAnalyzeBtn() {
    await this.click('#analyze');
  }
}
