# =========================================================
# Stage 1: Build Application
# =========================================================
FROM node:24-alpine AS build

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Build application
RUN npm run build

# Verify build output (optional, useful for debugging)
RUN ls -la

# =========================================================
# Stage 2: Nginx
# =========================================================
FROM nginx:alpine

# Remove default nginx content
RUN rm -rf /usr/share/nginx/html/*

# Copy build output
# Change this to /app/dist if your build creates dist folder
COPY --from=build /app/dev-dist/ /usr/share/nginx/html/

# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]