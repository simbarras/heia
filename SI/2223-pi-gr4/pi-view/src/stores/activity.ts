import {computed, reactive, ref} from 'vue'
import {defineStore} from 'pinia'
import {addActivity, getActivities, modifyActivity, publishActivity, getParticipants} from "@/service/myApi";
import type {Activity} from "@/stores/beans";

export const useActivityStore = defineStore('activity', () => {
    const activity_list = reactive<Activity[]>([])


    const published = ref(false)
    const filter_activity_list = computed(() => {

        return activity_list.filter(e => {
            if (published.value === false) {
                return true
            } else if (e.state === "Cancelled" || e.state === "Terminated") {
                return false
            } else if (e.state === "Published") {
                return true
            }
        })
    })
    const updateFilters = (selected_published: boolean) => {
        published.value = selected_published
    }

    function addActivityStore(activity: Activity) {
        addActivity(activity).then(res => {
            console.log(res)
        }).then(() => {
            updateActivities().then()
        })
    }

    function addAndPublishActivityStore(activity: Activity) {
        addActivity(activity).then(res => {
            console.log(`Add activity: ${res}`)
            publishActivity(res.id).then(res => {
                console.log(`Publish activity: ${res}`)
            }).then(() => {
                console.log("Update activities")
                updateActivities().then()
            })
        })
    }

    function publishActivityStore(id: String) {
        publishActivity(id).then(res => {
            console.log(res)
        }).then(() => {
            updateActivities().then()
        })
    }

    async function modifyActivityStore(activity: Activity) {
        console.log("Modify activity: " + activity.id)
        try {
            let res = await modifyActivity(activity)

            return null
        } catch (e) {
            return "Another user has modified this activity"
        } finally {
            await updateActivities()
        }

    }

    function modifyAndPublishActivityStore(activity: Activity) {
        modifyActivity(activity).then(res => {
            console.log(`Modify activity: ${res}`)
            publishActivity(res.id).then(res => {
                console.log(`Publish activity: ${res}`)
            }).then(() => {
                console.log("Update activities")
                updateActivities().then()
            })
        })
    }

    const updateActivities = async () => {
        const res = await getActivities()
        activity_list.splice(0, activity_list.length)

        res.forEach(e => {
            if (e.dateList === null) {
                e.state = "Published"
                e.stateColor = "Green"
            } else {
                e.state = "Draft"
                e.stateColor = "Gray"
            }
            e.occursEntities.forEach(e => {
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
            })
            activity_list.push(e)
        })
        activity_list.sort((a, b) => {
            return a.name.localeCompare(b.name)
        })

    }

    const getParticipantsActivity = async (id: String, date: Date) => {
        const res = await getParticipants(id, date)
        return res
    }

    updateActivities().then();

    return {
        getParticipantsActivity,
        addActivityStore,
        addAndPublishActivityStore,
        publishActivityStore,
        modifyActivityStore,
        modifyAndPublishActivityStore,
        activity_list,
        filter_activity_list,
        updateFilters,
        updateActivities
    }
})

