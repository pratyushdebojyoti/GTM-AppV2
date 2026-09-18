import React, { createContext, useContext, useEffect, useState } from 'react';
import {
  onAuthStateChanged,
  signInWithEmailAndPassword,
  signOut as fbSignOut,
  User
} from 'firebase/auth';
import { doc, getDoc, setDoc } from 'firebase/firestore';
import { auth, db } from '../services/firebase';
import type { AgencyUser } from '../types';

interface AuthContextType {
  currentUser: AgencyUser | null;
  firebaseUser: User | null;
  loading: boolean;
  isAdmin: boolean;
  error: string | null;
  loginAsAdmin: (email: string, pass: string) => Promise<void>;
  signOut: () => Promise<void>;
  enableDemoAdmin: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [currentUser, setCurrentUser] = useState<AgencyUser | null>(null);
  const [firebaseUser, setFirebaseUser] = useState<User | null>(null);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, async (user) => {
      setFirebaseUser(user);
      if (user) {
        try {
          // Verify against Firestore `/users/{uid}`
          const userDoc = await getDoc(doc(db, 'users', user.uid));
          if (userDoc.exists()) {
            const userData = userDoc.data() as AgencyUser;
            setCurrentUser(userData);
            setError(null);
          } else {
            // New user doc creation defaults securely to CLIENT role.
            // Admin role can only be assigned via admin custom claims or Firestore administrative security rules.
            const newUserData: AgencyUser = {
              id: user.uid,
              email: user.email || '',
              displayName: user.displayName || user.email?.split('@')[0] || 'User',
              role: 'CLIENT',
              createdAtEpoch: Date.now(),
              updatedAtEpoch: Date.now()
            };
            await setDoc(doc(db, 'users', user.uid), newUserData);
            setCurrentUser(newUserData);
            setError(null);
          }
        } catch (err: any) {
          console.error('Failed to verify user authorization:', err);
          setError(err.message || 'Authorization error occurred');
        }
      } else {
        // Check if demo admin is stored in session
        const demoStored = sessionStorage.getItem('gotech_demo_admin');
        if (demoStored) {
          try {
            setCurrentUser(JSON.parse(demoStored));
          } catch {
            setCurrentUser(null);
          }
        } else {
          setCurrentUser(null);
        }
      }
      setLoading(false);
    });

    return () => unsubscribe();
  }, []);

  const loginAsAdmin = async (email: string, pass: string) => {
    setError(null);
    setLoading(true);
    try {
      const cred = await signInWithEmailAndPassword(auth, email, pass);
      const userDoc = await getDoc(doc(db, 'users', cred.user.uid));
      if (!userDoc.exists()) {
        throw new Error('User record not found in system database.');
      }
      const data = userDoc.data() as AgencyUser;
      if (data.role !== 'ADMIN') {
        await fbSignOut(auth);
        throw new Error('Access denied: Unauthorized access. This console requires verified ADMIN privileges.');
      }
      setCurrentUser(data);
    } catch (err: any) {
      setError(err.message || 'Authentication failed');
      throw err;
    } finally {
      setLoading(false);
    }
  };

  const enableDemoAdmin = () => {
    const demoAdmin: AgencyUser = {
      id: 'admin_master_demo',
      email: 'executive@gotechmedia.com',
      displayName: 'Alexander Sterling (Managing Partner)',
      role: 'ADMIN',
      company: 'GoTech Media Global LLC',
      createdAtEpoch: Date.now(),
      updatedAtEpoch: Date.now()
    };
    sessionStorage.setItem('gotech_demo_admin', JSON.stringify(demoAdmin));
    setCurrentUser(demoAdmin);
    setError(null);
  };

  const signOut = async () => {
    sessionStorage.removeItem('gotech_demo_admin');
    await fbSignOut(auth);
    setCurrentUser(null);
  };

  const isAdmin = currentUser?.role === 'ADMIN';

  return (
    <AuthContext.Provider
      value={{
        currentUser,
        firebaseUser,
        loading,
        isAdmin,
        error,
        loginAsAdmin,
        signOut,
        enableDemoAdmin
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
