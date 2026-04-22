import type { NextConfig } from "next";

const isProd = process.env.NODE_ENV === 'production';

const nextConfig: NextConfig = {
  output: 'export',
  basePath: isProd ? '/Personal-Website' : '',
  assetPrefix: isProd ? '/Personal-Website' : '',
  images: {
    unoptimized: true,
  },
};

export default nextConfig;
