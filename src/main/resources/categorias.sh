
#!/bin/bash

# curl -X POST http://localhost:8080/api/categories/admin \
#      -H "Authorization: Bearer <TOKEN>" \
#      -H "Content-Type: application/json" \
#      -d '{"name": "Categoria 1","description":"Categoria generica"}'
#
# The API endpoint URL
API_URL="http://localhost:8080/api/categories/admin"
TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJlY29tbWVyY2UtYXBpIiwic3ViIjoicEBwLmNvbSIsInJvbGUiOiJBRE1JTiIsImV4cCI6MTc3MzY5MDMwMH0.VC8Nz2PXa5SY0ccOrvawccjMLM-QMa0GeAb3VjXp0SY"

# Read the input JSON array from a file (input.json)
# jq -c '.[]' outputs each JSON object on a single line, which is ideal for looping
jq -c '.[]' categorias.json | while read -r json_object; do
  echo "Sending POST request with data: $json_object"

  # Send the POST request using curl
  curl --header "Content-Type: application/json" \
       --header "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJlY29tbWVyY2UtYXBpIiwic3ViIjoicEBwLmNvbSIsInJvbGUiOiJBRE1JTiIsImV4cCI6MTc3MzY5MDMwMH0.VC8Nz2PXa5SY0ccOrvawccjMLM-QMa0GeAb3VjXp0SY" \
       --request POST \
       --data "$json_object" \
       "$API_URL"
       # Optional: Redirect output to a file if needed (e.g., > response.log)
done
