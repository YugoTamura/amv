import BasePageElement from '@arch/BasePageElement';

export default class TopPageElement extends BasePageElement {
  get pageNameKey() {
    return 'home';
  }

  async open() {
    await super.open('/');
  }

  isLinkVisibleByName(name?: string) {
    return this.isLinkVisible('#codebases', name ?? '');
  }

  async clickNewCodebaseLink() {
    await this.click('#addNewCodebase');
  }

  async clickCodebaseIdLink(name?: string) {
    await this.clickLink(name ?? '');
  }
}
