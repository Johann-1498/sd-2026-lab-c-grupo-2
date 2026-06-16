FROM node:20-alpine
WORKDIR /app
COPY package.json .
RUN apk add --no-cache openssl
RUN npm install
COPY index.js .
EXPOSE 3003
CMD ["node", "index.js"]
