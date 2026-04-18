# # Stage 1: Build React App
# FROM node:20 AS build

# # Set working directory
# WORKDIR /app

# # Copy package.json and package-lock.json to install dependencies
# COPY package*.json ./

# # Install dependencies
# RUN npm install

# # Copy the entire project
# COPY . .

# # Build the React app
# RUN npm run build

# # Stage 2: Serve with Nginx
# FROM nginx:alpine

# # Set working directory (optional, for clarity)
# WORKDIR /usr/share/nginx/html

# # Copy build output from Stage 1 to Nginx's web root directory
# COPY --from=build /app/build .

# # Copy a custom Nginx configuration to use port 3000
# COPY nginx.conf /etc/nginx/conf.d/default.conf

# # Expose port 3000 for the Nginx server
# EXPOSE 6000

# # Start Nginx in the foreground
# CMD ["nginx", "-g", "daemon off;"]

 # Stage 1: Build Vite App
FROM node:20 AS build

# Set working directory
WORKDIR /app

# Copy dependency files
COPY package*.json ./

# Install dependencies
RUN npm install

# Copy project files
COPY . .

# Build the app (Vite outputs to /app/dist by default)
RUN npm run build

# Stage 2: Serve with Nginx
FROM nginx:alpine

# Remove default nginx static files
RUN rm -rf /usr/share/nginx/html/*

# Copy build output from Stage 1
COPY --from=build /app/dist /usr/share/nginx/html

# Copy custom nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port (match this with nginx.conf)
EXPOSE 80

# Start Nginx
CMD ["nginx", "-g", "daemon off;"]
