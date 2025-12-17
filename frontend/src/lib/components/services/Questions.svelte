<script lang="ts">
    import { onMount } from "svelte";
    import type { Question } from "$models/Question";
    import { QuestionForm } from "$models/forms/QuestionCreationForm";
    import { createQuestion, getServiceQuestions } from "$services/questionService";
    import Spinner from "$lib/components/global/Spinner.svelte";
    import Icon from "$icons";
    import { Pagination } from '@skeletonlabs/skeleton-svelte';
    import {t} from "$lib/i18n/i18n"
    import Title from "$lib/components/global/Title.svelte";
    import type {User} from "$models/User";
    import {goto} from "$app/navigation";
    import {base} from "$app/paths";
    import FormError from "$lib/components/global/forms/FormError.svelte";

    export let serviceId: number;
    export let isOwner: boolean = false;
    export let user: User = null;

    let questions: Question[] = [];
    let page = 1;

    let lastPage = 1;

    let loading = true;

    let form = new QuestionForm({ serviceId, question: "" });
    let errors: Record<string, string> = {};
    let sendError: string | null = null;

    async function loadQuestions(pageNum: number) {
        loading = true;

        const response = await getServiceQuestions(serviceId, pageNum);

        questions = response.items;
        lastPage = response.links.last ?? 1;
        page = pageNum;

        loading = false;
    }

    onMount(() => loadQuestions(page));

    async function submitQuestion() {
        if(!user) {
            goto(`${base}/login`);
        }
        sendError = null;
        errors = {};

        form.serviceId = serviceId;

        errors = form.validateQuestionForm();
        if (Object.keys(errors).length > 0) return;

        try {
            await createQuestion(form);
            form = new QuestionForm({ serviceId, question: "" });
            await loadQuestions(page);
        } catch {
            sendError = "Error sending question";
        }
    }

    $: pages = (() => {
        const last = lastPage;

        if (last <= 5) {
            return Array.from({ length: last }, (_, i) => i + 1);
        }

        if (page <= 2) {
            return [1, 2, 3, "ellipsis", last];
        }

        if (page >= last - 1) {
            return [1, "ellipsis", last - 2, last - 1, last];
        }

        return [1, "ellipsis", page - 1, page, page + 1, "ellipsis", last];
    })();

    function escapeHtml(text: string): string {
        const div = document.createElement("div");
        div.textContent = text;
        return div.innerHTML;
    }
</script>

{#if loading}
    <Spinner />
{:else}
    <div>
        <Title text={$t("service.q&r")}/>
        {#if !isOwner}
            <div class="mb-5">
                <div class="flex">
                    <textarea
                        class="bg-surface-200 mt-5 w-2/3 p-5 rounded-2xl"
                        maxlength="255"
                        bind:value={form.question}
                        placeholder={$t("service.ask")}
                    ></textarea>
                    <div
                        on:click={submitQuestion}
                        class="cursor-pointer flex items-center justify-center p-2 hover:opacity-80 text-2xl text-primary-500"
                    >
                        <Icon name="send"/>
                    </div>
                </div>
                {#if errors.question}
                    <FormError errorMessage={errors.question} />
                {/if}
            </div>
        {/if}

        {#if questions.length === 0}
            <div class="flex justify-center mt-10 text-surface-600">
                {#if isOwner}
                    <p>{$t("service.no-questions")}</p>
                {:else}
                    <p>{$t("service.first-to-ask")}</p>
                {/if}
            </div>
        {/if}

        {#if questions.length > 0}
            {#each questions as q}
                <div class="mb-1">
                    <label>{@html escapeHtml(q.question)}</label>
                    <label class="font-medium text-surface-600 ml-5">{q.date}</label>
                    {#if q.response}
                        <div class="flex text-surface-700 align-middle mt-2">
                            <Icon name="responseArrow" class="mr-1"/>
                            <p>{q.response}</p>
                        </div>
                    {/if}
                </div>
                <hr class="border-t border-gray-300 my-4" />

            {/each}
        {/if}

        {#if lastPage > 1}
        <div class="w-full flex justify-center my-6">
        <Pagination
            count={lastPage}
            pageSize={1}
            {page}
            onPageChange={(event) => {
                page = event.page;
                loadQuestions(page);
            }}
            class="m-15 flex justify-center items-center space-x-2"
        >
        <Pagination.PrevTrigger>
            <Icon name="leftArrow"/>
        </Pagination.PrevTrigger>
        <div class="flex justify-center items-center space-x-2 my-4">
            {#each pages as p}
                {#if p === 'ellipsis'}
                    <span class="w-8 h-8 flex items-center justify-center">…</span>
                {:else}
                    <button
                        class={`w-8 h-8 flex items-center justify-center rounded cursor-pointer hover:shadow-lg
                        ${p === page ? 'bg-primary-500 text-white font-bold' : 'bg-gray-200 text-black'}`}
                        on:click={() => loadQuestions(p)}
                    >
                        {p}
                    </button>
                {/if}
            {/each}
        </div>
        <Pagination.NextTrigger>
            <Icon name="rightArrow"/>
        </Pagination.NextTrigger>
        </Pagination>
        </div>
        {/if}
    </div>
{/if}
