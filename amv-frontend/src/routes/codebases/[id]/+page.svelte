<script lang="ts">
  import { goto, invalidateAll } from '$app/navigation';
  import ApiHandler from '$lib/arch/api/ApiHandler';
  import { messageStore } from '$lib/arch/global/MessageStore';
  import CodebaseForm from '$lib/domain/codebases/CodebaseForm.svelte';
  import type { PageProps } from './$types';
  import { env } from '$env/dynamic/public';
  import StatusIcon from '$lib/components/StatusIcon.svelte';
  import { m } from '$lib/paraglide/messages';

  let { data }: PageProps = $props();
  let codebase = $state(data.codebase);
  let analyzing = $state(data.codebase.status.analyzing);

  // svelte-ignore state_referenced_locally
  if (analyzing) {
    reciveStatus(codebase.id);
  }

  $effect(() => {
    codebase = data.codebase;
  });

  async function handleAfterSave(id?: string) {
    await invalidateAll();
  }

  async function handleAfterDelete() {
    await goto(`/codebases/`);
  }

  async function analyze() {
    await ApiHandler.handle<void>(fetch, (api) => api.codebases.analyze(codebase.id));

    reciveStatus(codebase.id);
  }

  function reciveStatus(codebaseId: string) {
    const wsBaseUri = env.PUBLIC_BACKEND_WS_URL || 'ws://' + new URL(window.location.href).host;
    const wsUri = `${wsBaseUri}/chat/${codebaseId}`;
    const websocket = new WebSocket(wsUri);

    websocket.addEventListener('open', () => {
      console.log('CONNECTED');
      analyzing = true;
    });

    websocket.addEventListener('message', async (event) => {
      console.log(`RECEIVED: ${event.data}`);
      await invalidateAll();
    });

    websocket.addEventListener('close', async () => {
      console.log('DISCONNECTED');
      analyzing = false;
      await invalidateAll();
      messageStore.show(m.analyzeCompleted());
    });

    websocket.addEventListener('error', (event) => {
      console.log(`ERROR: ${event}`);
    });
  }
</script>

<CodebaseForm
  bind:codebase
  updateMode={true}
  {handleAfterSave}
  {handleAfterDelete}
  {renderExtra}
  {analyze}
/>

{#snippet renderExtra()}
  <div>
    <span>{m.status()}</span>
    {#if analyzing}
      <span aria-busy="true">{m.analyzing()}</span>
    {/if}
  </div>

  <ol>
    <li><StatusIcon done={codebase.status.checkedOut} /> {m.checkedOut()}</li>
    <li><StatusIcon done={codebase.status.projectsLoaded} /> {m.projectsLoaded()}</li>
    <li><StatusIcon done={codebase.status.metadataExtracted} /> {m.metadataExtracted()}</li>
  </ol>

  {m.projects()}
  <article>
    {#each codebase.projects as project, i}
      {#if i > 0},
      {/if}
      <span>
        {project.name}
      </span>
    {/each}
  </article>
{/snippet}
