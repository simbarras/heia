import {computed, reactive, ref} from 'vue'
import {defineStore} from 'pinia'
import {getOccurs, updateParticipationForUser} from "@/service/myApi";
import type {Occurs} from "@/stores/beans";

export const useOccursStore = defineStore('occurs', () => {
        const occurs_list = reactive<Occurs[]>([])
        const categories = ref<string[]>([])
        const date_range = ref<Date[]>([])
        const participate = ref(false)
        const states = ref<string[]>([])
        const filtered_occurs_list = computed(() => {
            return occurs_list.filter(e => {
                const dateInRange = !date_range.value.length || (e.dateActivity >= date_range.value[0] && e.dateActivity <= date_range.value[1]);
                const categoryIncluded = !categories.value.length || categories.value.includes(e.category);
                const isParticipating = !participate.value || e.participate;
                const stateIncluded = !states.value.length || states.value.includes(e.state);
                return dateInRange && categoryIncluded && isParticipating && stateIncluded;
            })
        })

        const updateFilters = (selected_date_range: Date[], selectedCategories: string[], selected_participate: boolean, states_selected: string[]) => {
            categories.value = selectedCategories;
            participate.value = selected_participate;
            states.value = states_selected || [];
            date_range.value = selected_date_range || [];
        }

        const updateOccurs = async () => {
            console.log("Update occurs")
            getOccurs()
                .catch((e) => {
                    console.log(e)
                    occurs_list.splice(0, occurs_list.length)
                })
                .then((res) => {
                    // Clear the list
                    occurs_list.splice(0, occurs_list.length)
                    res?.forEach(e => {
                        e.dateActivity = new Date(e.dateActivity)
                        if (e.canceled) {
                            e.state = "Cancelled"
                            e.stateColor = "red"
                        } else if (e.dateActivity < new Date()) {
                            e.state = "Terminated"
                            e.stateColor = "gray"
                        } else {
                            e.state = "Opened"
                            e.stateColor = "green"
                        }
                        occurs_list.push(e)
                    })
                    occurs_list.sort((a, b) => {
                        return a.dateActivity.getTime() - b.dateActivity.getTime()
                    })
                })
            const res = await getOccurs()
            // Clear the list
            occurs_list.splice(0, occurs_list.length)
            res.forEach(e => {
                e.dateActivity = new Date(e.dateActivity)
                console.log(e.localization)

                if (e.canceled) {
                    e.state = "Cancelled"
                    e.stateColor = "red"
                } else if (new Date(e.dateActivity) < new Date()) {
                    e.state = "Terminated"
                    e.stateColor = "gray"
                } else {
                    e.state = "Opened"
                    e.stateColor = "green"
                }
                occurs_list.push(e)
            })
            occurs_list.sort((a, b) => {
                return b.dateActivity.getTime() - a.dateActivity.getTime()
            })
        }
        const updateParticipation = async (occursId: string, date: Date) => {
            let oldOccurs = filtered_occurs_list.value.find(e => e.id === occursId && e.dateActivity.getTime() === date.getTime());
            if (oldOccurs !== undefined) {
                try {
                    let res = await updateParticipationForUser({
                        activityId: oldOccurs.id,
                        dateOccurs: oldOccurs.dateActivity.toISOString(),
                    })
                    console.log(res)
                    if (res) {
                        // @ts-ignore
                        oldOccurs.participate = res.participate
                        // @ts-ignore
                        oldOccurs.nbPerson = res.nbPerson
                    }
                } catch (e) {
                    console.log("Not enough place")
                    // @ts-ignore
                    oldOccurs.nbPerson = oldOccurs.maxPerson
                    return "Not enough place"
                }

            } else {
                throw new Error("Occurs not found")
            }
            return null;
        }


        updateOccurs().then();

        return {occurs_list, filtered_occurs_list, updateFilters, updateOccurs, updateParticipation}
    }
)




