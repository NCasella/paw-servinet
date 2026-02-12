<script lang="ts">
    import { onMount } from "svelte";
    import Spinner from "$lib/components/global/Spinner.svelte";
    import Title from "$lib/components/global/Title.svelte";
    import { Pagination } from "@skeletonlabs/skeleton-svelte";
    import { t } from "$lib/i18n/i18n";
    import { goto } from '$app/navigation';

    import type { Question } from "$models/Question";
    import { QuestionResponseForm } from "$models/forms/QuestionResponseForm";
    import { getRespondentQuestions, respondQuestion } from "$services/questionService";
    import { getServiceById } from "$services/serviceService";
    import Icon from "$icons";
    import FormError from "$lib/components/global/forms/FormError.svelte";
    import {base} from "$app/paths";

    let questions: Question[] = [];
    let loading = true;

    let page = 1;
    let lastPage = 1;

    const forms: Record<number, QuestionResponseForm> = {};
    const errors: Record<number, Record<string, string>> = {};

    const services: Record<number, string> = {};

    async function loadQuestions(pageNum: number) {
        loading = true;
        try {
            const response = await getRespondentQuestions(pageNum);
            questions = response.items;
            lastPage = response.links.last ?? 1;
            page = pageNum;

            questions.forEach(q => {
                if (!forms[q.questionId]) forms[q.questionId] = new QuestionResponseForm();
                if (!errors[q.questionId]) errors[q.questionId] = {};
            });

            const serviceIds = questions.map(q => q.serviceId).filter(id => !services[id]);
            await Promise.all(serviceIds.map(async id => {
                const service = await getServiceById(id);
                services[id] = service.serviceName;
            }));
        } finally {
            loading = false;
        }
    }

    async function submitResponse(qId: number) {
        const form = forms[qId];
        const formErrors = form.validateQuestionResponseForm();

        if (Object.keys(formErrors).length > 0) {
            errors[qId] = formErrors;
            return;
        }

        await respondQuestion(qId, form);
        await loadQuestions(page);
    }

    function gotoService(serviceId: number) {
        goto(`${base}/services/${serviceId}`);
    }

    $: pages = (() => {
        const last = lastPage;
        if (last <= 5) return Array.from({ length: last }, (_, i) => i + 1);
        if (page <= 2) return [1, 2, 3, "ellipsis", last];
        if (page >= last - 1) return [1, "ellipsis", last - 2, last - 1, last];
        return [1, "ellipsis", page - 1, page, page + 1, "ellipsis", last];
    })();

    onMount(() => loadQuestions(page));
</script>

<svelte:head>
    <title>{$t('title.questions')}</title>
</svelte:head>

{#if loading}
    <Spinner />
{:else}
    <div class="page p-4">
        <Title text={$t("title.questions")} />

        {#if questions.length === 0}
            <h4 class="notification-header text-lg font-semibold flex items-center space-x-2">
                <Icon name="noNotification" />
                <span>{$t("questions.none")}</span>
            </h4>
        {:else}
            <h4 class="notification-header text-lg font-semibold flex items-center space-x-2 mb-4">
                <Icon name="notification" />
                <span>{$t("questions.new")}</span>
            </h4>

            {#each questions as q (q.questionId)}
                <div class="question-box mb-4 p-4 rounded-xl bg-gray-100 border border-gray-300">
                    <p class="qst-service font-semibold mb-2 cursor-pointer text-blue-600 hover:underline"
                       on:click={() => gotoService(q.serviceId)}>
                        {services[q.serviceId]}
                    </p>

                    <div class="qst-date-box flex mb-2">
                        <span class="mr-5">{q.question}</span>
                        <span class="text-gray-500">{q.date}</span>
                    </div>

                    <div class="flex items-center space-x-2">
                        <input
                                type="text"
                                bind:value={forms[q.questionId].response}
                                placeholder={$t("questions.answer")}
                                class="flex-1 p-2 border border-gray-300 rounded-xl"
                        />
                        <button
                                class="bg-primary-500 font-bold text-white px-4 py-2 rounded-xl hover:opacity-80"
                                on:click={() => submitResponse(q.questionId)}
                        >
                            {$t("questions.send")}
                        </button>
                    </div>

                    {#if errors[q.questionId]?.response}
                        <FormError errorMessage={errors[q.questionId]?.response} />
                    {/if}
                </div>
            {/each}

            <Pagination
                    count={lastPage}
                    pageSize={1}
                    {page}
                    onPageChange={(event) => loadQuestions(event.page)}
                    class="m-15 flex justify-center items-center space-x-2"
            >
                <Pagination.PrevTrigger>
                    <Icon name="leftArrow"/>
                </Pagination.PrevTrigger>

                <div class="flex space-x-2">
                    {#each pages as p}
                        {#if p === 'ellipsis'}
                            <span class="w-8 flex items-center justify-center">…</span>
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
        {/if}
    </div>
{/if}
