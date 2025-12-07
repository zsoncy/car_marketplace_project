# Build stage
FROM node:22 AS build
LABEL authors="vesabence"
WORKDIR /app
# Install dependencies using lockfile
COPY package*.json ./
RUN npm ci
# Copy source and build
COPY . .
RUN npm run build

# Runtime stage using Nginx with API proxy
FROM nginx:1.27-alpine AS runner
# Copy built assets to Nginx html directory
COPY --from=build /app/dist /usr/share/nginx/html
# Provide Nginx config to serve SPA and proxy /api to backend container
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Expose the port Nginx will listen on (matches our nginx.conf)
EXPOSE 3000

# Run Nginx in the foreground
CMD ["nginx", "-g", "daemon off;"]
