import { io } from 'socket.io-client';

const SOCKET_URL = import.meta.env.VITE_SOCKET_URL || 'http://localhost:8085';

let socket = null;

export const getSocket = () => {
  if (!socket) {
    socket = io(SOCKET_URL, {
      transports: ['websocket', 'polling'],
      autoConnect: true,
      reconnection: true,
      reconnectionAttempts: 10,
      reconnectionDelay: 1000
    });

    socket.on('connect', () => {
      console.log('Socket.IO connected with ID:', socket.id);
    });

    socket.on('disconnect', (reason) => {
      console.log('Socket.IO disconnected:', reason);
    });

    socket.on('connect_error', (err) => {
      console.warn('Socket.IO connection error:', err.message);
    });
  }
  return socket;
};

export const joinCentreRoom = (centreId) => {
  const skt = getSocket();
  if (centreId) {
    const roomName = `centre_${centreId}`;
    skt.emit('join_room', roomName);
    console.log(`Joined room ${roomName}`);
  }
};

export const subscribeToQueueUpdates = (centreId, onUpdate) => {
  const skt = getSocket();
  joinCentreRoom(centreId);

  const handleUpdate = (data) => {
    console.log('Real-time queue:updated received:', data);
    if (onUpdate) onUpdate(data);
  };

  skt.on('queue:updated', handleUpdate);

  return () => {
    skt.off('queue:updated', handleUpdate);
  };
};

export default getSocket;
