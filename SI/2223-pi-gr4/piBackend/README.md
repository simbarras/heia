# Liste des routes
## Infos
- GET /api/v1/health
- GET /api/v1/health/db
- POST /api/v1/health/db
  - body: { "version: "string" }

## Activités
- GET /api/v1/activities
- GET /api/v1/activities/:id
- POST /api/v1/activities
  - body: { "name": "string", "description": "string", "date": "string", "duration": "string", "distance": "string", "type": "string", "user_id": "string" }
- PUT /api/v1/activities/:id
  - body: { "name": "string", "description": "string", "date": "string", "duration": "string", "distance": "string", "type": "string", "user_id": "string" }

## Occurrences
- GET /api/v1/occurrs
- GET /api/v1/occurrences/:id