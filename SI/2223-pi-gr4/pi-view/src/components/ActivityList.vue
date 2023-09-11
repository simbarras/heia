<template>
    <v-row>
        <v-col>
            <v-checkbox
                    v-model="seePublished"
                    label="Published"
                    @change="updateFilters(seePublished)"
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
                        <p class="bold_title">Participant min/max</p>
                    </v-col>
                    <v-col>
                        <p class="bold_title">State</p>
                    </v-col>
                    <v-col>
                        <p class="bold_title">Supervisors</p>
                    </v-col>

                    <v-col>
                        <p class="bold_title">Category</p>
                    </v-col>

                </v-row>
                <template v-slot:actions>
                </template>
            </v-expansion-panel-title>
        </v-expansion-panel>

        <v-expansion-panel
                v-for="(activity, i) in filter_activity_list"
                :key="i"
                hide-actions
        >
            <v-expansion-panel-title>
                <v-row
                        align="center"
                        class="spacer"
                >
                    <v-col>
                        {{ activity.name }}
                    </v-col>

                    <v-col>
                        {{ activity.minPerson }}/{{ activity.maxPerson }}
                    </v-col>

                    <v-col>
                        <v-chip
                                :color="activity.state === 'Draft' ? 'gray' : 'green'"
                                class="ms-0 me-2"
                                label
                                small
                        >
                            {{ activity.state }}
                        </v-chip>
                    </v-col>

                    <v-col>
                        {{ activity.responsables }}
                    </v-col>


                    <v-col>
                        {{ activity.category }}
                    </v-col>


                </v-row>
            </v-expansion-panel-title>
            <v-expansion-panel-text>
                <v-row>
                    <v-col>
                        <v-col>
                            <ActivityDialog
                                    :activity="activity"

                                    color="primary"
                                    v-if="activity.state === 'Draft'">

                            </ActivityDialog>
                        </v-col>

                        <v-col>
                            <v-btn
                                    color="primary"
                                    v-if="activity.state === 'Draft'"
                                    @click="publish(activity.id)"
                            >
                                Publish
                            </v-btn>
                        </v-col>
                        <v-col>
                            Id: {{ activity.id }}
                        </v-col>

                        <v-col v-if="activity.state === 'Draft'">
                            Dates: {{ activity.dateList }}
                        </v-col>
                        <v-col v-else>
                            List of dates:
                            <v-list>
                                <v-list-item
                                        v-for="(occurs, i) in activity.occursEntities"
                                        :key="i"
                                >
                                    <v-card-text v-if="occurs.state === opened">Date: {{ occurs.dateActivity }} Opened
                                    </v-card-text>
                                    <v-card-text v-else>Date: {{ occurs.dateActivity }}</v-card-text>

                                </v-list-item>
                            </v-list>


                        </v-col>
                        <v-col>Tools:
                            <v-list>
                                <v-list-item
                                        v-for="(tool, i) in activity.tools"
                                        :key="i"
                                >
                                    <v-card-text>{{ tool }}</v-card-text>
                                </v-list-item>
                            </v-list>

                        </v-col>
                        <v-col>Consumables:
                            <v-list>
                                <v-list-item
                                        v-for="(consumable, i) in activity.consumables"
                                        :key="i"
                                >
                                    <v-card-text>{{ consumable }}</v-card-text>
                                </v-list-item>
                            </v-list>
                        </v-col>
                    </v-col>
                </v-row>

            </v-expansion-panel-text>
        </v-expansion-panel>
    </v-expansion-panels>
</template>

<script setup>
import ActivityDialog from "@/components/ActivityDialog.vue";

import {storeToRefs} from "pinia";
import {useActivityStore} from "@/stores/activity";
import {watch, watchEffect} from "vue";
import {useLoginStore} from "@/stores/login";
import {publishActivity} from "@/service/myApi";
import {ref} from 'vue';
import {endOfMonth, endOfYear, startOfMonth, startOfYear, subMonths} from 'date-fns';
import {useCategoryStore} from "@/stores/category";
import {useOccursStore} from "@/stores/occurs";

const activityStore = useActivityStore()
const updateFilters = activityStore.updateFilters
const publish = activityStore.publishActivityStore
const {activity_list, filter_activity_list} = storeToRefs(activityStore)

const loginStore = useLoginStore()
const {user_token} = storeToRefs(loginStore)

const seePublished = ref(false)

const categoryStore = useCategoryStore()
const {category_list} = storeToRefs(categoryStore)
const date = ref();

// Watch for changes in the activity list
watchEffect(() => {
    console.log("Activity list changed")
    console.log(activity_list.value)
})

const opened = "Opened"
const canceled = "Canceled"
const terminated = "Terminated"

const presetRanges = ref([
    {label: 'Today', range: [new Date(), new Date()]},
    {label: 'This month', range: [startOfMonth(new Date()), endOfMonth(new Date())]},
    {
        label: 'Last month',
        range: [startOfMonth(subMonths(new Date(), 1)), endOfMonth(subMonths(new Date(), 1))],
    },
    {label: 'This year', range: [startOfYear(new Date()), endOfYear(new Date())]},
    {
        label: 'This year (slot)',
        range: [startOfYear(new Date()), endOfYear(new Date())],
        slot: 'yearly',
    },
]);

console.log(activity_list)

</script>
<style>
.non-clickable {
    pointer-events: none
}

.non-clickable .v-expansion-panel-header__title {
    color: #757575;
}

.bold_title {
    font-weight: bold;
    font-size: 1.2rem;
}
</style>
