set -e
cd /app/src/main/frontend
npm install --no-optional
npm run build
cd /app
exec "$@"