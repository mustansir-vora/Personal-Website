const basePath = process.env.NODE_ENV === 'production' ? '/Personal-Website' : '';

export function assetPath(path: string): string {
  return `${basePath}${path}`;
}

export default basePath;
