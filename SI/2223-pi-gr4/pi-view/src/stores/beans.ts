export class Occurs {
    id: string = ""
    dateActivity: Date = new Date()
    canceled: boolean = false
    name: string = ""
    maxPerson: number = 0
    minPerson: number = 0
    nbPerson: number = 0
    responsables: string = ""
    image: string = ""
    participate: boolean = false
    price: number = 0.0
    localization: {
        lat: number,
        lng: number
    } = {lat: 46.7922781, lng: 7.1603362}

    category: string = ""
    tools: string[] = []
    consumables: string[] = []

    state: string = ""
    stateColor: string | null = null
}

export class User {
    email: string = ""
    firstname: string = ""
    lastname: string = ""
}

export class OccursEntity {
    id: string = ""
    dateActivity: string = ""
    canceled: boolean = false

}

export class Participant {
    participant: string = ""
    idActivity: string = ""
    dateActivity: Date = new Date()
}

export class Activity {
    id: string = ""
    name: string = ""
    description: string = ""
    responsables: string = ""
    image: string = ""
    price: number = 1.1
    category: string = ""
    tools: string[] = []
    consumables: string[] = []
    maxPerson: number = 0
    minPerson: number = 0
    localization: {
        lat: number,
        lng: number
    } = {lat: 46.7922781, lng: 7.1603362}

    state: string = "Draft"
    stateColor: string = "Gray"

    dateList: Date[] = []

    occursEntities: Occurs[] = []

    modificationId: number = 0

}

