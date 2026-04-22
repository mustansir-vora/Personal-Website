import type { NextConfig } from "next";
//test-comment to trigger workflow
const nextConfig: NextConfig = {
  output: 'export',
  images: {
    unoptimized: true,
  },
};

export default nextConfig;
