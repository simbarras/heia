
<script setup>
import { ref } from 'vue'
const profile = ref({
  name: 'John Doe',
  email: 'john.doe@edu.hefr.ch',
  phone: '+41 79 123 45 67',
  creationDate: '2021-01-01',
  bbuserid: '123456789',
  usergroups: ['group1', 'group2', 'group3'],
  apikeys: ['apikey1', 'apikey2', 'apikey3'],
})
</script>
<template>
  <v-container>
    <v-row>
      <v-col cols="4">

        <v-card>
          <v-card-title>
            <h1>User Profile</h1>
          </v-card-title>
          <v-card-text>
            <v-row>
              <v-col cols="12">
                <v-text-field
                    v-model="profile.name"
                    label="Name"
                />
              </v-col >
            </v-row>
            <v-row>
              <v-col cols="12">
                <v-text-field
                    v-model="profile.email"
                    label="Email"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="12">
                <v-text-field
                    v-model="profile.phone"
                    label="Phone"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="12">
                <v-text-field
                    v-model="profile.creationDate"
                    label="Creation Date"
                    disabled
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="12">
                <v-text-field
                    v-model="profile.bbuserid"
                    label="BBuser ID"
                    disabled
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="12">
                <h2>User Groups</h2>
                <ul>
                  <li v-for="group in profile.usergroups" :key="group">{{ group }}</li>
                </ul>
              </v-col>
            </v-row>
            <v-row>
              <v-col cols="12">
                <h2>API Keys</h2>
                <ul>
                  <li v-for="key in profile.apikeys" :key="key">{{ key }}</li>
                </ul>
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

