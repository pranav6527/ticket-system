# ---------- Build stage ----------
FROM node:20-alpine AS build

WORKDIR /app

# Enable pnpm (comes with Node via corepack)
RUN corepack enable

# Copy only dependency files first (cache optimization)
COPY package.json pnpm-lock.yaml ./

# Install deps (reproducible, fast)
RUN pnpm install --frozen-lockfile

# Copy rest of the source
COPY . .

# Build frontend
RUN pnpm run build


# ---------- Runtime stage ----------
FROM nginx:alpine

COPY --from=build /app/dist /usr/share/nginx/html

