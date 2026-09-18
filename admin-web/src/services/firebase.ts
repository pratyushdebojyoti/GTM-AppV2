import { initializeApp, getApps, getApp } from 'firebase/app';
import { getAuth } from 'firebase/auth';
import { getFirestore } from 'firebase/firestore';
import { getStorage } from 'firebase/storage';

// Project credentials configured to match the GoTech Media Firebase backend
const firebaseConfig = {
  apiKey: import.meta.env.VITE_FIREBASE_API_KEY || "AIzaSyD-Y1PVlrx5zb2-dEL4K-LPO_k2r5IoYXM",
  authDomain: import.meta.env.VITE_FIREBASE_AUTH_DOMAIN || "gotech-media.firebaseapp.com",
  projectId: import.meta.env.VITE_FIREBASE_PROJECT_ID || "gotech-media",
  storageBucket: import.meta.env.VITE_FIREBASE_STORAGE_BUCKET || "gotech-media.firebasestorage.app",
  messagingSenderId: import.meta.env.VITE_FIREBASE_MESSAGING_SENDER_ID || "442261143835",
  appId: import.meta.env.VITE_FIREBASE_APP_ID || "1:442261143835:web:fac8ded0709076912893be"
};

const app = getApps().length === 0 ? initializeApp(firebaseConfig) : getApp();

export const auth = getAuth(app);
export const db = getFirestore(app);
export const storage = getStorage(app);
export default app;
