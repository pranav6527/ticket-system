type JwtPayload = {
  role?: string;
  [key: string]: unknown;
};

const decodeBase64Url = (value: string) => {
  const normalized = value.replace(/-/g, '+').replace(/_/g, '/');
  const padded = normalized.padEnd(
    normalized.length + ((4 - (normalized.length % 4)) % 4),
    '='
  );
  return atob(padded);
};

export const getJwtPayload = (token: string | null): JwtPayload | null => {
  if (!token) return null;
  const parts = token.split('.');
  if (parts.length < 2) return null;

  try {
    const decoded = decodeBase64Url(parts[1]);
    return JSON.parse(decoded) as JwtPayload;
  } catch {
    return null;
  }
};
