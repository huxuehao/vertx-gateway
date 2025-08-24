<script setup lang="ts">
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'
import { EditorState } from '@codemirror/state'
import { EditorView, lineNumbers, keymap } from '@codemirror/view'
import { java } from '@codemirror/lang-java'
import { oneDark } from '@codemirror/theme-one-dark'
import { defaultKeymap, indentMore, indentLess, undo, redo } from '@codemirror/commands'
import { autocompletion, CompletionContext } from '@codemirror/autocomplete'

interface Props {
  modelValue: string,
  title: string,
  readOnly?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  readOnly: false // 默认非只读模式
})
const emit = defineEmits<{
  (event: 'update:modelValue', value: string): void
  (event: 'close', value: void): void
}>()

const editorElement = ref<HTMLElement | null>(null)
let editorView: EditorView | null = null

// 自定义代码提示
const javaCompletions = (context: CompletionContext) => {
  const word = context.matchBefore(/\w*/)
  if (!word || word.from === word.to && !context.explicit) return null

  return {
    from: word.from,
    options: [
      { label: 'public', type: 'keyword' },
      { label: 'private', type: 'keyword' },
      { label: 'class', type: 'keyword' },
      { label: 'Autowired', type: 'keyword' },
      { label: 'Resource', type: 'keyword' },
      { label: 'void', type: 'keyword' },
      { label: 'static', type: 'keyword' },
      { label: 'main', type: 'function' },
      { label: 'System', type: 'class' },
      { label: 'out', type: 'property' },
      { label: 'println', type: 'function' },
      { label: 'String', type: 'class' },
      { label: 'int', type: 'type' },
      { label: 'Integer', type: 'type' },
      { label: 'boolean', type: 'type' },
      { label: 'Boolean', type: 'type' },
      { label: 'long', type: 'type' },
      { label: 'Long', type: 'type' },
      { label: 'double', type: 'type' },
      { label: 'Double', type: 'type' },
      { label: 'float', type: 'type' },
      { label: 'Float', type: 'type' },
      { label: 'true', type: 'constant' },
      { label: 'false', type: 'constant' },
      { label: 'new', type: 'keyword' },
      { label: 'return', type: 'keyword' },
    ]
  }
}

// 自动补全 {}
const autoCloseBrackets = EditorView.inputHandler.of((view, from, to, insert) => {
  if (insert === '{') {
    // 插入 {}
    view.dispatch({
      changes: { from, to, insert: '{}' },
      selection: { anchor: from + 1 } // 将光标移动到 {} 中间
    })
    return true
  }
  return false
})

// 配置编辑器扩展
const extensions = [
  lineNumbers(), // 显示行号
  java(), // Java 语法高亮
  oneDark, // 主题
  EditorState.tabSize.of(4), // 设置 Tab 键为 2 个空格
  EditorState.readOnly.of(props.readOnly), // 动态设置只读模式
  // EditorView.lineWrapping, // 启用换行
  autocompletion({ override: [javaCompletions] }), // 启用代码提示
  autoCloseBrackets, // 自动补全 {}
  keymap.of([
    ...defaultKeymap, // 默认快捷键
    { key: 'Tab', run: indentMore }, // Tab 键增加缩进
    { key: 'Shift-Tab', run: indentLess }, // Shift + Tab 减少缩进
    { key: 'Mod-z', run: undo }, // Ctrl+Z（撤销）
    { key: 'Mod-y', run: redo }, // Ctrl+Y（重做）
  ]),
  EditorView.updateListener.of((update) => {
    if (update.docChanged) {
      const content = update.state.doc.toString()
      emit('update:modelValue', content)
    }
  })
]

// 初始化编辑器
onMounted(() => {
  if (editorElement.value) {
    let doc_ = props.modelValue || ""
    // if(!doc_) {
    //   doc_ = ""
    //   emit('update:modelValue', doc_)
    // }
    const state = EditorState.create({
      doc: doc_,
      extensions,
    })

    editorView = new EditorView({
      state,
      parent: editorElement.value
    })
  }
})

// 处理外部值变化
watch(() => props.modelValue, (newVal) => {
  if (newVal !== editorView?.state.doc.toString()) {
    editorView?.dispatch({
      changes: {
        from: 0,
        to: editorView.state.doc.length,
        insert: newVal
      }
    })
  }
})

// 清理资源
onBeforeUnmount(() => {
  editorView?.destroy()
})

const handleClose = () => {
  emit("close")
};
</script>

<template>
  <div class="code-btn">
    <span class="title">{{ title || '代码编辑器' }} </span>
    <slot name="button"></slot>
    <el-button icon="CircleClose" type="danger" @click="handleClose">关闭</el-button>
  </div>
  <div class="editor-container">
    <div ref="editorElement"></div>
  </div>
</template>

<style scoped>
.code-btn {
  width: 100vw;
  text-align: right;
  background-color: #282c34; 
  height: 45px;
  padding: 10px 10px 0 0 ;
  position: relative;
}
.title {
  position: absolute;
  left: 15px;
  font-size: 16px;
  color: #D6D6D6;
  font-weight: bold;
}
.code-box {
  background-color: #282c34;
  height: calc(100vh - 45px);
  overflow: auto;
  position: relative;
}
</style>
<style>
.editor-container {
  position: relative;
  overflow: hidden;
}

.cm-editor {
  height: calc(100vh - 45px);
  font-size: 16px;
  letter-spacing: 1px;
}

.cm-scroller {
  font-family: Monaco, Consolas, 'Courier New', monospace;
  /* 确保内容可以滚动 */
  overflow: auto;
  /* 禁用自动换行 */
  white-space: pre; 
}

/* 自定义滚动条样式 */
.cm-scroller::-webkit-scrollbar {
  height: 8px; /* 横向滚动条高度 */
  width: 8px;
}

.cm-scroller::-webkit-scrollbar-track {
  background: #282c34; /* 滚动条轨道背景色 */
  border-radius: 5px; /* 圆角 */
}

.cm-scroller::-webkit-scrollbar-thumb {
  background: rgba(255,255,255, 0.1); /* 滚动条滑块背景色 */
  border-radius: 5px; /* 圆角 */
}

.cm-scroller::-webkit-scrollbar-thumb:hover {
  background: rgba(255,255,255, 0.15); /* 滚动条滑块悬停背景色 */
}

.cm-gutters {
  background-color: #282c34;
  border-right: 1px solid #3a3f4b;
}

.cm-content {
  /* 确保换行生效 */
  /* white-space: pre-wrap;  */
  /* 禁用自动换行 */
  white-space: pre; 
}
</style>