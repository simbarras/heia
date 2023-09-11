<template>
    <v-row justify="end">
        <v-dialog
                v-model="dialog"
                persistent
                width="1024"
        >
            <template v-slot:activator="{ props }">
                <v-btn
                        color="primary"
                        v-bind="props"
                        v-if="propss.activity === undefined"
                >
                    Add activity
                </v-btn>
                <v-btn
                        color="primary"
                        v-bind="props"
                        v-else
                >
                    Modify activity
                </v-btn>
            </template>
            <v-card>
                <v-card-title>
                    <span class="text-h5">Add activity</span>
                </v-card-title>
                <v-card-text>
                    <v-form v-model="valid">
                        <v-row>
                            <v-col
                                    cols="12"
                                    sm="6"
                                    md="4"
                            >
                                <v-text-field
                                        v-model="refActivity.name"
                                        label="Name*"
                                        required
                                        :rules="nameRules"
                                >

                                </v-text-field>
                            </v-col>
                            <v-col
                                    cols="12"
                                    sm="6"
                                    md="8"
                            >
                                <v-label>Dates:</v-label>
                                <VueDatePicker
                                        v-model="refActivity.dateList"
                                        multi-dates
                                        :flow="['calendar', 'time']"
                                        :min-date="new Date()"
                                />


                            </v-col>
                        </v-row>
                        <v-row>

                            <v-col
                                    cols="12"
                                    sm="6"
                                    md="4"
                            >
                                <v-text-field
                                        v-model="refActivity.responsables"
                                        label="Responsable*"
                                        hint=""
                                        persistent-hint
                                        required
                                        :rules="responsablesRules"
                                ></v-text-field>
                            </v-col>
                            <v-select
                                    v-model="refActivity.category"
                                    :items="category_list"
                                    label="Category*" required
                                    :rules="categoryRules"
                            ></v-select>
                        </v-row>
                        <v-row>

                            <v-col cols="12"
                                   sm="6"
                                   md="4">
                                <v-select
                                        v-model="refActivity.tools"
                                        :items="tools_list"
                                        label="Tools"
                                        multiple
                                        hint="Pick the tools you need"
                                        persistent-hint
                                ></v-select>
                            </v-col>
                            <v-col cols="12"
                                   sm="6"
                                   md="4">
                                <v-select
                                        v-model="refActivity.consumables"
                                        :items="consumable_list"
                                        label="Consumables"
                                        multiple
                                        hint="Pick the consumables you need"
                                        persistent-hint
                                ></v-select>
                            </v-col>
                            <v-col cols="12"
                                   sm="2"
                                   md="2">
                                <v-text-field
                                        v-model.number="refActivity.minPerson"
                                        label="Min. people*" required
                                        :rules="minPersonRules"
                                >

                                </v-text-field>
                            </v-col>
                            <v-col cols="12"
                                   sm="2"
                                   md="2">
                                <v-text-field
                                        v-model.number="refActivity.maxPerson"
                                        label="Max. people*" required
                                        :rules="maxPersonRules"
                                >
                                </v-text-field>
                            </v-col>
                        </v-row>
                        <v-row>
                            <v-col cols="12"
                                   sm="2"
                                   md="2">
                                <v-text-field
                                        v-model.number="refActivity.price"
                                        label="Amount*"
                                        prefix="CHF"
                                        required
                                        :rules="priceRules"
                                ></v-text-field>
                            </v-col>
                        </v-row>
                    </v-form>
                    <small>*indicates required field</small>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn
                            color="blue-darken-1"
                            variant="text"
                            @click="dialog = false"
                    >
                        Cancel
                    </v-btn>
                    <v-btn
                            v-if="propss.activity === undefined"
                            color="blue-darken-1"
                            variant="text"
                            @click="addActivity(); dialog = false"
                            :disabled="!valid"
                    >
                        Add
                    </v-btn>
                    <v-btn
                            v-if="propss.activity !== undefined"
                            color="blue-darken-1"
                            variant="text"

                            @click="modifyActivity(); dialog = false"

                    >
                        Modify
                    </v-btn>
                    <v-btn
                            v-if="propss.activity === undefined"
                            color="blue-darken-1"
                            variant="text"
                            @click="addAndPublishActivity(); dialog = false"
                            :disabled="!valid"
                    >
                        Add and Publish
                    </v-btn>

                    <v-btn
                            v-if="propss.activity !== undefined"
                            color="blue-darken-1"
                            variant="text"
                            @click="modifyAndPublishActivity(); dialog = false"
                    >
                        Modify and Publish
                    </v-btn>

                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-row>
</template>

<script setup>
import {storeToRefs} from "pinia";
import {useToolsStore} from "@/stores/tools";
import {useConsumableStore} from "@/stores/consumable";
import {useCategoryStore} from "@/stores/category";
import {ref, watch} from 'vue';
import {Activity} from "@/stores/beans";
import {useActivityStore} from "@/stores/activity";
import {useErrorsStore} from "@/stores/error";

const errorsStore = useErrorsStore()
const alertUser = errorsStore.alertUser

const consumableStore = useConsumableStore()
const {consumable_list} = storeToRefs(consumableStore)

const categoryStore = useCategoryStore()
const {category_list} = storeToRefs(categoryStore)

const toolsStore = useToolsStore()
const {tools_list} = storeToRefs(toolsStore)

const {
    addActivityStore,
    modifyActivityStore,
    activity_list,
    publishActivityStore,
    addAndPublishActivityStore,
    modifyAndPublishActivityStore
} = useActivityStore()


const valid = ref(false);

console.log(toolsStore)

const propss = defineProps({
    activity: {
        type: Activity,
        required: false,
    },
})

const refActivity = ref(new Activity());

const test = propss.activity?.dateList.toString();
console.log(test)

if (test === undefined) {
    refActivity.value.dateList = [];
} else {
    refActivity.value.dateList = JSON.parse(test).map(test => new Date(test));
}
let dialog = ref(false);
console.log("Props activity: ")
console.log(propss.activity?.name)
refActivity.value.name = propss.activity?.name;
refActivity.value.minPerson = propss.activity?.minPerson;
refActivity.value.maxPerson = propss.activity?.maxPerson;
refActivity.value.category = propss.activity?.category;
refActivity.value.responsables = propss.activity?.responsables;
refActivity.value.tools = propss.activity?.tools ?? [];
refActivity.value.consumables = propss.activity?.consumables ?? [];
refActivity.value.price = propss.activity?.price;
refActivity.value.id = propss.activity?.id;
refActivity.value.modificationId = propss.activity?.modificationId;

function addActivity() {
    console.log(refActivity.value.dateList)
    addActivityStore(refActivity.value)

}

function addAndPublishActivity() {
    addAndPublishActivityStore(refActivity.value)
}

async function modifyActivity() {
    const result = await modifyActivityStore(refActivity.value)
    if (result !== null){
        alertUser(result)
    }
}

async function modifyAndPublishActivity() {
    const result = await modifyAndPublishActivityStore(refActivity.value)
    if (result !== null){
        alertUser(result)
    }
}


const nameRules = [
    v => !!v || "Username is required"
];

const dateRules = [
    v => !!v || "Date is required"
];

const responsablesRules = [
    v => !!v || "Responsables is required"
];

const categoryRules = [
    v => !!v || "Category is required"
];

const minPersonRules = [
    v => !!v || "Min person is required",
    v => v > 0 || "Min person must be greater than 0",
    // if maxPerson is not defined, it is not possible to compare it
    v => refActivity.value.maxPerson === undefined || v <= refActivity.value.maxPerson || "Min person must be less than max person"

];

const maxPersonRules = [
    v => !!v || "Max person is required",
    v => v > 0 || "Max person must be greater than 0",
    v => v >= refActivity.value.minPerson || "Max person must be greater than min person"
];

const priceRules = [
    v => !!v || "Price is required",
    v => v > 0 || "Price must be greater than 0"
];

// List of the date before today
const disabledDates = {
    to: new Date()
};


</script>