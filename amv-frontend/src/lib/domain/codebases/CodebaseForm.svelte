<script lang="ts">
  import type { CodebaseModel } from '$lib/arch/api/Api';
  import ApiHandler from '$lib/arch/api/ApiHandler';
  import FormValidator from '$lib/arch/form/FormValidator';
  import InputField from '$lib/arch/form/InputField.svelte';
  import TextArea from '$lib/arch/form/TextArea.svelte';
  import { messageStore } from '$lib/arch/global/MessageStore';
  import * as m from '$lib/paraglide/messages';
  import { string, date, boolean, number } from 'yup';
  import type { Snippet } from 'svelte';

  interface Props {
    codebase: CodebaseModel;
    updateMode?: boolean;
    handleAfterSave: (id?: string) => Promise<void>;
    handleAfterDelete?: (id?: string) => Promise<void>;
    renderExtra?: () => ReturnType<Snippet>;
    analyze?: () => Promise<void>;
  }

  let {
    codebase = $bindable(),
    updateMode = false,
    handleAfterSave,
    handleAfterDelete = async (id) => {},
    renderExtra,
    analyze = async () => {}
  }: Props = $props();

  let analyzing = $derived(codebase.status?.analyzing ?? false);
  let showDeleteModal = $state(false);

  const spec = {
    url: string().required()
  };

  const form = FormValidator.createForm(spec, save, del, analyze);

  async function save() {
    const response = await ApiHandler.handle<string>(fetch, (api) =>
      updateMode ? api.codebases.update(codebase.id, codebase) : api.codebases.save(codebase)
    );

    if (response) {
      await handleAfterSave(response);
      messageStore.show(m.saved());
    }
  }

  async function del() {
    const response = await ApiHandler.handle<string>(fetch, (api) =>
      api.codebases.delete(codebase.id, codebase)
    );

    if (response) {
      await handleAfterDelete();
      messageStore.show(m.deleted());
    }
  }
</script>

<form class="container narrow" use:form>
  <div>
    <TextArea id="url" label={m.url()} bind:value={codebase.url} required={true} />
  </div>
  <div>
    <InputField id="name" label={m.name()} bind:value={codebase.name} />
  </div>
  <div>
    <InputField id="token" label={m.token()} bind:value={codebase.token} />
  </div>
  {#if renderExtra}
    {@render renderExtra()}
  {/if}
  <div class="grid">
    <div>
      <!--  TODO: fix SVQK also (function's name properties are not unique )-->
      <!-- <button type="submit" id="save" data-handler={save.name}> -->
      <input
        type="submit"
        id="save"
        value={updateMode ? m.update() : m.register()}
        data-handler={save}
        disabled={analyzing}
      />
    </div>
    {#if updateMode}
      <div>
        <input
          type="submit"
          id="analyze"
          value={m.analyze()}
          data-handler={analyze}
          disabled={analyzing}
        />
      </div>
      <div>
        <input
          type="button"
          id="del"
          value={m.delete()}
          onclick={() => (showDeleteModal = true)}
          disabled={analyzing}
        />
      </div>
    {/if}
  </div>
  {#if showDeleteModal}
    <dialog open>
      <article>
        <header>
          <p>
            <strong>{m.delete()} : {codebase.name}</strong>
          </p>
        </header>
        <p>{m.deleteConfirmation()}</p>
        <div class="grid">
          <div>
            <input
              class="secondary"
              type="button"
              id="cancel"
              value={m.cancel()}
              onclick={() => (showDeleteModal = false)}
            />
          </div>
          <div>
            <input type="submit" id="delete-confirm" data-handler={del} value={m.delete()} />
          </div>
        </div>
      </article>
    </dialog>
  {/if}
</form>
