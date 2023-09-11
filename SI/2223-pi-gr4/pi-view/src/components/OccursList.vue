<template>
    <v-row>
        <v-col>
            <VueDatePicker v-model="date" range :preset-ranges="presetRanges" :enable-time-picker="false"
                           @update:model-value="updateFilters(date,categories, seeMyActivities)">
                <template #yearly="{ label, range, presetDateRange }">
                    <span @click="presetDateRange(range)">{{ label }}</span>
                </template>
            </VueDatePicker>
        </v-col>
        <v-col>
            <v-select
                    v-model="categories"
                    :items="category_list"
                    label="Category"
                    multiple
                    chips
                    small-chips
                    clearable
                    @update:model-value="updateFilters(date, categories, seeMyActivities, states)"
            ></v-select>
        </v-col>

        <v-col>
            <v-select
                    v-model="states"
                    :items="state_list"
                    label="State"
                    multiple
                    chips
                    small-chips
                    clearable
                    @update:model-value="updateFilters(date, categories, seeMyActivities, states)"
            ></v-select>
        </v-col>
        <v-col
                v-if="user_role === 'participant'"
        >
            <v-checkbox
                    v-model="seeMyActivities"
                    label="My activities"
                    @change="updateFilters(date, categories, seeMyActivities, states)"
            >
            </v-checkbox>
        </v-col>
    </v-row>
    <v-expansion-panels
            variant="popout"
            class="pa-4">
        <v-expansion-panel
                class="non-clickable"
        >
            <v-expansion-panel-title>
                <v-row
                        align="center"
                        class="spacer"
                        no-gutters
                >
                    <v-col>
                        <p class="bold_title">Name</p>
                    </v-col>
                    <v-col>
                        <p class="bold_title">Participant</p>
                    </v-col>
                    <v-col>
                        <p class="bold_title">State</p>
                    </v-col>
                    <v-col>
                        <p class="bold_title">Supervisors</p>
                    </v-col>
                    <v-col>
                        <p class="bold_title">Date</p>
                    </v-col>
                    <v-col>
                        <p class="bold_title">Category</p>
                    </v-col>

                </v-row>
                <template v-slot:actions>
                </template>
            </v-expansion-panel-title>
        </v-expansion-panel>
        <v-expansion-panel v-if="!occurs_charged">
            <v-expansion-panel-content>
                <v-row
                        align="center"
                        class="spacer"
                        no-gutters
                >
                    <v-col>
                        <v-progress-linear
                                indeterminate
                                color="primary"
                        ></v-progress-linear>
                    </v-col>
                </v-row>
            </v-expansion-panel-content>
        </v-expansion-panel>

        <v-expansion-panel v-if="occurs_charged && (occurs_list === undefined || occurs_list.length === 0)"
                           class="non-clickable">
            <v-expansion-panel-title
            >
                <v-row
                        align="center"
                        class="spacer"
                        no-gutters
                >
                    <v-col>
                        <label>Nothing to see here</label>
                    </v-col>
                </v-row>
            </v-expansion-panel-title>
        </v-expansion-panel>
        <v-expansion-panel
                v-for="(occur, i) in filtered_occurs_list"
                :key="i"
                hide-actions
        >

            <v-expansion-panel-title>
                <v-row
                        align="center"
                        class="spacer"
                >
                    <v-col>
                        {{ occur.name }}
                    </v-col>

                    <v-col>
                        {{ occur.nbPerson }}/{{ occur.maxPerson }} (min {{ occur.minPerson }})
                    </v-col>

                    <v-col>
                        <v-chip
                                :color="occur.stateColor"
                                class="ms-0 me-2"
                                label
                                small
                        >
                            {{ occur.state }}
                        </v-chip>
                    </v-col>

                    <v-col>
                        {{ occur.responsables }}
                    </v-col>

                    <v-col>
                        {{ occur.dateActivity.toLocaleDateString('en-GB') }}
                    </v-col>

                    <v-col>
                        {{ occur.category }}
                    </v-col>

                </v-row>
            </v-expansion-panel-title>
            <v-expansion-panel-text>
                Tools: {{ occur.tools.join(', ') }}
                <br>
                Consumables: {{ occur.consumables.join(', ') }}
                <br>
                Price: {{ occur.price }} CHF
                <br>
                Location: {{ occur.localization }}
                <v-btn v-if="user_role === 'participant' && occur.state === 'Opened' && occur.nbPerson >= occur.maxPerson && occur.participate"
                       @click="toggleParticipation(occur.id, occur.dateActivity)">
                    <p>Leave</p>
                </v-btn>
                <v-btn v-else-if="user_role === 'participant' && occur.state === 'Opened' && occur.nbPerson < occur.maxPerson"
                       @click="toggleParticipation(occur.id, occur.dateActivity)">
                    <p v-if="occur.participate">Leave</p>
                    <p v-else>Join</p>
                </v-btn>
                <v-btn v-else-if="user_role === 'participant' && occur.state === 'Opened' && occur.nbPerson >= occur.maxPerson && !occur.participate"
                       disabled>
                    <p>Full</p>
                </v-btn>

            </v-expansion-panel-text>
        </v-expansion-panel>
    </v-expansion-panels>
</template>

<script setup>

import {storeToRefs} from "pinia";
import {useOccursStore} from "@/stores/occurs";
import {ref, watch} from "vue";
import {useLoginStore} from "@/stores/login";
import {endOfMonth, endOfYear, startOfMonth, startOfYear, subMonths} from "date-fns";
import {useCategoryStore} from "@/stores/category";
import {useErrorsStore} from "@/stores/error";

const errorsStore = useErrorsStore()
const alertUser = errorsStore.alertUser
const occursStore = useOccursStore()
const {occurs_list, filtered_occurs_list} = storeToRefs(occursStore)
const updateFilters = occursStore.updateFilters
const updateParticipation = occursStore.updateParticipation

async function toggleParticipation(id, date) {
    console.log("Toggle participation: " + id + " at " + date)
    const result = await updateParticipation(id, date)
    console.log(result)
    if (result != null) {
        alertUser(result)
    }
}

watch(filtered_occurs_list, (value) => {
    occurs_charged.value = true
})

watch(occurs_list, (value) => {
    console.log(value)
    occurs_charged.value = true
})
const categories = ref([])
const date = ref([])
const seeMyActivities = ref(false)

const states = ref([])

const loginStore = useLoginStore()
const {user_role} = storeToRefs(loginStore);
const categoryStore = useCategoryStore()
const {category_list} = storeToRefs(categoryStore)
const state_list = ref(["Opened", "Cancelled", "Terminated"])

const occurs_charged = ref(filtered_occurs_list.value.length > 0)

const presetRanges = ref([
    {label: 'Today', range: [new Date(), new Date()]},
    {
        label: 'This week',
        range: [new Date(new Date().setDate(new Date().getDate() - new Date().getDay())), new Date(new Date().setDate(new Date().getDate() - new Date().getDay() + 6))],
    },
    {
        label: 'This month',
        range: [startOfMonth(new Date()), endOfMonth(new Date())]
    },
    {
        label: 'Next month',
        range: [startOfMonth(subMonths(new Date(), -1)), endOfMonth(subMonths(new Date(), -1))],
    },
    {label: 'This year', range: [startOfYear(new Date()), endOfYear(new Date())]},
]);

</script>

<style>
.non-clickable {
    pointer-events: none;
}

.non-clickable .v-expansion-panel-header__title {
    color: #757575;
}

.bold_title {
    font-weight: bold;
    font-size: 1.2rem;
}
</style>