import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  output: 'export',
  basePath: '/Personal-Website',
  assetPrefix: '/Personal-Website',
  images: {
    unoptimized: true,
  },
};

export default nextConfig;
