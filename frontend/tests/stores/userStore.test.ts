import { describe, it, expect, beforeEach } from 'vitest';
import { get } from 'svelte/store';
import { user, login, logout, getUser, type UserState } from '$stores/userStore';
import { User } from '$models/User';

// Helper function to create a mock user
function createMockUser(overrides: Partial<{
  userId: number;
  fullName: string;
  username: string;
  email: string;
  language: string;
  links?: { profilePic?: string };
  isProvider: boolean;
}> = {}): User {
  return new User({
    userId: 1,
    fullName: 'John Doe',
    username: 'johndoe',
    email: 'john@example.com',
    language: 'en',
    isProvider: false,
    ...overrides
  });
}

describe('userStore', () => {
  beforeEach(() => {
    // Reset the store to initial state before each test
    logout();
  });

  describe('initial state', () => {
    it('should have null user initially', () => {
      const state = get(user);
      expect(state.user).toBeNull();
    });
  });

  describe('login', () => {
    it('should set current user in store', () => {
      const mockUser = createMockUser();

      login(mockUser);

      const state = get(user);
      expect(state.user).toBe(mockUser);
      expect(state.user?.userId).toBe(1);
      expect(state.user?.fullName).toBe('John Doe');
      expect(state.user?.username).toBe('johndoe');
      expect(state.user?.email).toBe('john@example.com');
    });

    it('should replace existing user when logging in again', () => {
      const firstUser = createMockUser({ userId: 1, username: 'first' });
      const secondUser = createMockUser({ userId: 2, username: 'second' });

      login(firstUser);
      expect(get(user).user?.username).toBe('first');

      login(secondUser);
      expect(get(user).user?.username).toBe('second');
      expect(get(user).user?.userId).toBe(2);
    });
  });

  describe('logout', () => {
    it('should clear user state back to null', () => {
      const mockUser = createMockUser();
      login(mockUser);

      // Verify user is logged in
      expect(get(user).user).toBe(mockUser);

      // Logout
      logout();

      // Verify user is null
      expect(get(user).user).toBeNull();
    });

    it('should reset state after login', () => {
      const mockUser = createMockUser({
        userId: 42,
        fullName: 'Test User',
        username: 'testuser',
        email: 'test@example.com'
      });

      // Login first
      login(mockUser);
      expect(get(user).user).not.toBeNull();
      expect(get(user).user?.userId).toBe(42);

      // Then logout
      logout();

      // Verify complete reset
      const state = get(user);
      expect(state.user).toBeNull();
    });
  });

  describe('getUser', () => {
    it('should return current user synchronously', () => {
      const mockUser = createMockUser({
        userId: 123,
        fullName: 'Sync User'
      });

      login(mockUser);

      const result = getUser();
      expect(result).toBe(mockUser);
      expect(result?.userId).toBe(123);
      expect(result?.fullName).toBe('Sync User');
    });

    it('should return null when no user is logged in', () => {
      // Ensure logged out state
      logout();

      const result = getUser();
      expect(result).toBeNull();
    });

    it('should return null after logout', () => {
      const mockUser = createMockUser();
      login(mockUser);

      expect(getUser()).toBe(mockUser);

      logout();

      expect(getUser()).toBeNull();
    });
  });
});
