/**
 * websocket服务
 */

interface WsMessage {
  message: string;
  users: string[]
  title?: string;
  type?: 'success' | 'warning' | 'info' | 'error';
  speech?: boolean;
  speechTimes?: number,
  duration?: number,
  position?: 'top-right' | 'top-left' | 'bottom-right' | 'bottom-left'
}

export const initWebSocket = (app: any) => {
  const userId = "1111111111111111111"
  const hostname = window.location.hostname;
  const port = window.location.port;
  // 在连接URL中携带用户ID
  const socket = new WebSocket(`ws://127.0.0.1:8060/ws/notify?userId=${encodeURIComponent(userId)}`);

  socket.onmessage = (event) => {
    const data: WsMessage = JSON.parse(event.data);
    // 只有当当前用户在被推送列表中时才显示通知
    if (data.users.length ===0 || data.users.includes(userId)) {
      setTimeout(() => {
        app.config.globalProperties.$notifyWithSpeech({
            msg: data.message,
            title: data.title,
            type: data.type,
            speech: data.speech,
            speechTimes: data.speechTimes,
            duration: data.duration,
            position: data.position
          });
      },0)
    }
  };

  socket.onerror = (error) => {
    console.error('WebSocket error:', error);
  };

  socket.onclose = () => {
    console.log('WebSocket connection closed');
  };
};