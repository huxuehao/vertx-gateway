import { ElNotification } from "element-plus";

/**
 * 语音播报 + 消息提示插件
 */

interface SpeechNotifyOptions {
  msg: string; // 通知消息内容
  title?: string; // 通知标题
  speech?: boolean; // 是否启用语音播报
  speechTimes?: number | "loop"; // 语音播报次数
  type?: "success" | "warning" | "info" | "error";
  duration?: number; // 通知显示时长
  position?: "top-right" | "top-left" | "bottom-right" | "bottom-left";
}

// 活动通知映射表（用于防止重复通知）
const activeNotifications = new Map<
  string,
  {
    notify: ReturnType<typeof ElNotification>;
    utterance?: SpeechSynthesisUtterance;
    synth: SpeechSynthesis;
    speechTimes: number | "loop";
    currentCount: number;
  }
>();

const removeHTMLTags = (str: string) => {
  return str.replace(/<[^>]*>/g, '');
};

const SpeechNotifyPlugin = {
  install(app: any) {
    app.config.globalProperties.$notifyWithSpeech = (
      options: SpeechNotifyOptions
    ) => {
      const {
        msg,
        title = "系统通知",
        speech = true,
        speechTimes = 1,
        type = "info",
        duration = 0,
        position = "bottom-right",
      } = options;

      // 如果存在相同未关闭的通知，直接返回
      if (
        activeNotifications.has(msg) &&
        !activeNotifications.get(msg)?.notify.closed
      ) {
        return;
      }

      // 创建语音实例
      let utterance: SpeechSynthesisUtterance | undefined;
      const synth = window.speechSynthesis;

      if (speech) {
        utterance = new SpeechSynthesisUtterance(removeHTMLTags(msg));
        utterance.lang = "zh-CN";
        let currentCount = 0;

        utterance.onend = () => {
          const notification = activeNotifications.get(msg);
          if (notification) {
            currentCount++;
            if (speechTimes === "loop" || currentCount < speechTimes) {
              synth.speak(notification.utterance!);
            } else {
              // 语音播报完成，清理资源
              activeNotifications.delete(msg);
            }
          }
        };
      }

      // 创建通知实例
      const notify = ElNotification({
        title,
        message: msg,
        type,
        duration,
        position,
        dangerouslyUseHTMLString: true,
        onClose: () => {
          // 点击关闭
          const notification = activeNotifications.get(msg);
          if (notification) {
            // 结束语音
            notification.synth.cancel();
            // 清理资源
            activeNotifications.delete(msg);
          }
        },
      });

      // 保存实例
      activeNotifications.set(msg, {
        notify,
        utterance,
        synth,
        speechTimes,
        currentCount: 0,
      });

      // 开始语音播报
      if (utterance) {
        synth.speak(utterance);
      }
    };
  },
};

export default SpeechNotifyPlugin;
