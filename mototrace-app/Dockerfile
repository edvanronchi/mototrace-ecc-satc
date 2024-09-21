FROM node:20.17-alpine as build

WORKDIR /app

COPY package*.json ./

RUN npm ci -f
RUN npm install -g @angular/cli

COPY . .

RUN npm run build --configuration=production

FROM nginx:alpine

COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/mototrace/browser /usr/share/nginx/html

CMD ["nginx", "-g", "daemon off;"]