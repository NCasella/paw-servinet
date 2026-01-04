import { describe, it, expect, vi, beforeEach } from 'vitest';
import { writable } from 'svelte/store';

// Mock $app/environment
vi.mock('$app/environment', () => ({
  browser: true
}));

// Mock $stores/localStorageStore to return a simple writable store
vi.mock('$stores/localStorageStore', () => ({
  localStorageStore: <T>(key: string, initial: T) => writable<T>(initial)
}));

import { auth, setTokens, clearTokens, getAccessToken, getRefreshToken } from '$stores/auth';

describe('auth store', () => {
  beforeEach(() => {
    // Reset the store to initial state before each test
    clearTokens();
  });

  describe('setTokens', () => {
    it('should update access token', () => {
      setTokens({ accessToken: 'test-access-token' });

      expect(getAccessToken()).toBe('test-access-token');
      expect(getRefreshToken()).toBeNull();
    });

    it('should update refresh token', () => {
      setTokens({ refreshToken: 'test-refresh-token' });

      expect(getRefreshToken()).toBe('test-refresh-token');
      expect(getAccessToken()).toBeNull();
    });

    it('should update both tokens', () => {
      setTokens({
        accessToken: 'test-access-token',
        refreshToken: 'test-refresh-token'
      });

      expect(getAccessToken()).toBe('test-access-token');
      expect(getRefreshToken()).toBe('test-refresh-token');
    });
  });

  describe('clearTokens', () => {
    it('should reset to initial state with null tokens', () => {
      // First set some tokens
      setTokens({
        accessToken: 'test-access-token',
        refreshToken: 'test-refresh-token'
      });

      // Verify tokens are set
      expect(getAccessToken()).toBe('test-access-token');
      expect(getRefreshToken()).toBe('test-refresh-token');

      // Clear tokens
      clearTokens();

      // Verify tokens are null
      expect(getAccessToken()).toBeNull();
      expect(getRefreshToken()).toBeNull();
    });
  });

  describe('getAccessToken', () => {
    it('should return current access token', () => {
      setTokens({ accessToken: 'my-access-token' });

      expect(getAccessToken()).toBe('my-access-token');
    });

    it('should return null when not set', () => {
      expect(getAccessToken()).toBeNull();
    });
  });

  describe('getRefreshToken', () => {
    it('should return current refresh token', () => {
      setTokens({ refreshToken: 'my-refresh-token' });

      expect(getRefreshToken()).toBe('my-refresh-token');
    });

    it('should return null when not set', () => {
      expect(getRefreshToken()).toBeNull();
    });
  });
});
