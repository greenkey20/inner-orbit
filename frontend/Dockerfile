# Build Stage
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# Run Stage
FROM nginx:alpine
# 위에서 작성한 nginx 설정파일 복사
COPY nginx.conf /etc/nginx/conf.d/default.conf
# 빌드 결과물 복사 (Vite 기본 빌드 폴더는 dist)
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
