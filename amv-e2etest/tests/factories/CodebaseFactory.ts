import type { CodebaseModel } from '@api/Api';

export default class CodebaseFactory {
  static createTestCodebase() {
    return {
      name: 'amv-test-repository',
      url: 'https://github.com/project-au-lait/amv-test-repository.git',
      projects: [{ name: 'amv-test-repository' }]
    } as CodebaseModel;
  }
}
