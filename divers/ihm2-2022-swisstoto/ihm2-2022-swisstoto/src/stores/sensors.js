import { ref, computed } from 'vue'
import { defineStore } from 'pinia'

export const useSensorsStore = defineStore('sensors', () => {
  const sensors = ref([
    {id: 1, description: 'This is a test description 1', token: '123456789', unit: '%', tags: ['tag1', 'tag2'], createdAt: '2021-01', owner: 'owner1', name: 'sensor1'},
    {id: 2, description: 'This is a test description 2', token: '3904813', unit: '%', tags: ['tag3', 'tag4'], createdAt: '2021-01', owner: 'owner2', name: 'sensor2'},
    {id: 3, description: 'This is a test description 3', token: '79482708', unit: 'lux', tags: ['tag5', 'tag6', 'tag7', 'tag8'], createdAt: '2021-01', owner: 'owner3', name: 'sensor3'},
  ])

  const graphicSensors = ref([
    'sensbox/ms/in/06/temperature',
    'sensbox/ms/in/06/humidity',
    'sensbox/ms/in/06/pressure',
    'sensbox/ms/in/06/altitude',
    'sensbox/ms/in/06/acceleration',
    'sensbox/ms/in/06/gyroscope',
    'sensbox/ms/in/06/magnetometer',
    'sensbox/ms/in/06/uv',
    'sensbox/ms/in/06/ir',
    'sensbox/ms/in/06/ambient',
  ])

  function registerSensor(sensor) {
    if(sensor.id) {
      const sensorIndex = sensors.value.findIndex(s => s.id === sensor.id)
      // replace sensor attributes 'existOwnerGroup' whit 'owner' and delete 'newOwnerGroup'
      sensor.owner = sensor.existOwnerGroup
      delete sensor.existOwnerGroup
      delete sensor.newOwnerGroup
    
      console.log(sensor)
      
      sensors.value.splice(sensorIndex, 1, sensor)

      return
    }

    const newSensor = {
      id: sensors.value.length + 1,
      description: sensor.description,
      token: Math.floor(Math.random() * 100000000),
      unit: sensor.unit,
      tags: sensor.tags,
      createdAt: new Date().toISOString().slice(0, 10),
      owner: '',
      name: sensor.name,
    }

    if(sensor.existOwnerGroup !== '') {
      newSensor.owner = sensor.existOwnerGroup
    } else {
      newSensor.owner = sensor.newOwnerGroup
    }

    sensors.value.push(newSensor)
  }

  return { sensors, registerSensor, graphicSensors }
})
