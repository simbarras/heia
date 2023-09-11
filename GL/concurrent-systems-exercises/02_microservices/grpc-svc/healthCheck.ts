export class HealthCheck {
    status: string;

    constructor() {
        this.status = 'UNKNOWN';
    }

    /**
     * Set the status of the health check to SERVING
     */
    ready() {
        this.status = 'SERVING';
    }

    /**
     * Set the status of the health check to NOT_SERVING
     */
    notReady() {
        this.status = 'NOT_SERVING';
    }

    /**
     * Set the status of the health check to UNKNOWN
     */
    unknown() {
        this.status = 'UNKNOWN';
    }

    /**
     * Implements the check method of the health check service
     * @param call - The call object
     * @param callback - The callback function
     */
    check(call, callback) {
        callback(null, {status: this.status});
    }
}