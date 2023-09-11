const path = require('path');
const CopyPlugin = require("copy-webpack-plugin");

const {
  NODE_ENV = 'production',
} = process.env;
module.exports = {
  entry: {
    graphql: './graphql-svc/index.ts',
    grpc: './grpc-svc/index.ts',
  },
  mode: NODE_ENV,
  target: 'node',
  module: {
    rules: [
      {
        test: /\.ts$/,
        use: 'ts-loader',
      },
    ],
  },
  output: {
    path: path.resolve(__dirname, 'build'),
    filename: '[name]-svc/app.js'
  },
  resolve: {
    extensions: ['.ts', '.js'],
    fallback: {
      "mongodb-client-encryption": false,
      "@mongodb-js/zstd": false,
      "aws4": false,
      "kerberos": false,
      "aws-crt": false,
      "snappy": false,
    }
  },
  plugins: [
    new CopyPlugin({
      patterns: [
        { from: "grpc-svc/*.proto", to: "." },
      ],
    }),
  ],
}