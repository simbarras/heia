# 2223-PI-Gr4

## Getting Started

To get started with this project, You need to have access to a Kubernetes Cluster.
You can download the deployments files and the sql script from the release page.

### Start up

To configure the right kubeconfig, you must put them into the deployment folder with the name `kubeconfig`.
To use this config, run the following command:

```bash
export KUBECONFIG=./deployment/kubeconfig
```

First, you need to create a namespace and add the secret to access to the gitlab registry.
To do that, you can apply the file `deployment/config.yml` with the following command.
A more detailed explanation in available in the getting started in teams.

```bash
kubectl apply -f deployment/config.yml
```

Some tools need to be installed like Nginx Ingress Controller and Longhorn.
If you use Exoscale, you can run the following command to install them:

```bash
kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/exoscale/deploy.yaml 
kubectl apply -f https://raw.githubusercontent.com/longhorn/longhorn/v1.3.2/deploy/longhorn.yaml
```

### Database

Now, you need to create the database and apply the schema. To do that, you can apply the file `deployment/db.yml` with
the following command:

```bash
kubectl apply -f deployment/db.yml
```

To add the schema to the database, you need to connect to the database with a redirection of the port 5432 of the
database service to your local machine.
To do that, you can run the following command:

```bash
kubectl port-forward -n pi-gr4 service/db 5432:5432
```

Then you must run the sql script `sql/script.sql` with the user `postgres` and the password `wafonsadp` to create the
schema in the database `postgres-db`.
You can also use the `sql/testData.sql` to add some data to the database.

### Deployments

Then, you need to apply the deployment files. These files are located in the `deployment` folder and they are containing
the deployment, the service and the ingress of each application.
To automatically apply all the files, you can run the following command.
The first argument is the version of the image which is the short commit or latest and the second argument is the url of
the cluster.
If you use Exoscale, you can find the url in the Exoscale console into the loadbalancer section.

```bash
sh deployment/setup.sh <version> <url>
```

Now you can access to the frontend application with the following url: `http://app.<url>` and the backend application
with the following url: `http://app.<url>/api`.

