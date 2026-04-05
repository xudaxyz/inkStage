package com.inkstage.utils;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.inkstage.utils.model.SentenceScore;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 文章摘要生成工具类
 * 使用HanLP进行文本分析和摘要生成
 */
@Slf4j
public class SummaryGenerator {

    private static final int MAX_SUMMARY_LENGTH = 180;
    private static final int MAX_SENTENCES = 3;

    /**
     * 生成文章摘要
     *
     * @param content 文章内容
     * @return 生成的摘要
     */
    public static String generateSummary(String content) {
        if (content == null || content.isEmpty()) {
            return null;
        }

        // 去除Markdown和HTML格式
        String plainText = removeFormatting(content);
        if (plainText.isEmpty()) {
            return null;
        }

        // 如果内容较短, 直接返回
        if (plainText.length() <= MAX_SUMMARY_LENGTH) {
            return plainText;
        }

        // 分割句子
        List<String> sentences = splitSentences(plainText);
        if (sentences.isEmpty()) {
            return plainText.substring(0, MAX_SUMMARY_LENGTH) + "...";
        }

        // 计算句子权重
        Map<String, Integer> wordFreq = calculateWordFrequency(plainText);
        List<SentenceScore> sentenceScores = calculateSentenceScores(sentences, wordFreq);

        // 选择得分最高的句子, 然后按原文顺序排列
        // 1. 排序句子得分
        sentenceScores.sort(Comparator.comparingInt(SentenceScore::score).reversed());

        // 2. 选择前N个得分最高的句子
        List<SentenceScore> topSentences = new ArrayList<>();
        Set<String> selectedSentences = new HashSet<>();
        int count = 0;

        for (SentenceScore ss : sentenceScores) {
            if (!selectedSentences.contains(ss.sentence())) {
                topSentences.add(ss);
                selectedSentences.add(ss.sentence());
                if (++count >= MAX_SENTENCES) {
                    break;
                }
            }
        }

        // 3. 按原文顺序排列句子
        Map<String, Integer> sentencePositions = new HashMap<>();
        for (int i = 0; i < sentences.size(); i++) {
            sentencePositions.put(sentences.get(i), i);
        }

        topSentences.sort(Comparator.comparingInt(ss -> sentencePositions.getOrDefault(ss.sentence(), Integer.MAX_VALUE)));

        // 4. 构建摘要
        StringBuilder summary = new StringBuilder();
        for (SentenceScore ss : topSentences) {
            String sentence = ss.sentence();

            if (summary.length() + sentence.length() > MAX_SUMMARY_LENGTH) {
                // 尝试截断句子以适应长度
                int remainingLength = MAX_SUMMARY_LENGTH - summary.length();
                if (remainingLength > 0) {
                    String truncatedSentence = truncateSentence(sentence, remainingLength);
                    summary.append(truncatedSentence);
                }
                break;
            }

            summary.append(sentence);
        }

        // 确保摘要以完整句子结束
        String result = summary.toString();
        if (result.length() > MAX_SUMMARY_LENGTH) {
            result = result.substring(0, MAX_SUMMARY_LENGTH) + "...";
        }

        return result;
    }

    /**
     * 去除Markdown和HTML格式
     *
     * @param content 带Markdown和HTML格式的内容
     * @return 纯文本内容
     */
    private static String removeFormatting(String content) {
        // 去除HTML标签
        content = content.replaceAll("<[^>]*>", "");
        // 去除标题
        content = content.replaceAll("#+\\s+", "");
        // 去除粗体
        content = content.replaceAll("\\*\\*|__", "");
        // 去除斜体
        content = content.replaceAll("[*_]", "");
        // 去除代码块
        content = content.replaceAll("`{1,3}[\\s\\S]*?`{1,3}", "");
        // 去除链接, 保留文本
        content = content.replaceAll("\\[([^]]+)]\\([^)]+\\)", "$1");
        // 去除图片
        content = content.replaceAll("!\\[([^]]*)]\\([^)]+\\)", "");
        // 替换换行为空格
        content = content.replaceAll("\\n+", " ");
        // 去除多余空格
        content = content.replaceAll("\\s+", " ").trim();
        return content;
    }

    /**
     * 分割句子
     *
     * @param text 文本内容
     * @return 句子列表
     */
    private static List<String> splitSentences(String text) {
        List<String> sentences = new ArrayList<>();
        try {
            // 句子分隔符, 包括中文和英文的句号、感叹号、问号
            String regex = "[。！？.!?]";
            String[] parts = text.split(regex);

            for (String part : parts) {
                part = part.trim();
                if (part.length() > 5) { // 过滤太短的句子
                    sentences.add(part);
                }
            }

            if (!sentences.isEmpty()) {
                return sentences;
            }
        } catch (Exception e) {
            log.warn("分割句子时出错: {}", e.getMessage());
        }
        return Collections.singletonList(text);
    }

    /**
     * 计算词频
     *
     * @param text 文本内容
     * @return 词频映射
     */
    private static Map<String, Integer> calculateWordFrequency(String text) {
        Map<String, Integer> wordFreq = new HashMap<>();
        List<Term> terms = StandardTokenizer.segment(text);

        for (Term term : terms) {
            // 过滤停用词和标点符号
            if (!isStopWord(term.word) && !isPunctuation(term.word)) {
                wordFreq.put(term.word, wordFreq.getOrDefault(term.word, 0) + 1);
            }
        }

        return wordFreq;
    }

    /**
     * 计算句子得分
     *
     * @param sentences 句子列表
     * @param wordFreq  词频映射
     * @return 句子得分列表
     */
    private static List<SentenceScore> calculateSentenceScores(List<String> sentences, Map<String, Integer> wordFreq) {
        List<SentenceScore> sentenceScores = new ArrayList<>();

        for (int i = 0; i < sentences.size(); i++) {
            String sentence = sentences.get(i);
            int score = 0;

            // 位置权重：首句和前几句权重更高
            if (i == 0) {
                score += 15; // 首句权重最高
            } else if (i < 3) {
                score += 10; // 前几句权重较高
            } else if (i == sentences.size() - 1) {
                score += 8; // 末句权重较高
            }

            // 词频权重
            List<Term> terms = StandardTokenizer.segment(sentence);
            int termScore = 0;
            for (Term term : terms) {
                termScore += wordFreq.getOrDefault(term.word, 0);
            }
            // 归一化词频得分, 避免长句子得分过高
            int avgTermScore = !terms.isEmpty() ? termScore / terms.size() : 0;
            score += avgTermScore * 2;

            // 长度权重：中等长度的句子权重更高
            int length = sentence.length();
            if (length >= 20 && length <= 120) {
                score += 8;
            } else if (length >= 10 && length < 20) {
                score += 4;
            } else if (length > 120 && length <= 200) {
                score += 2;
            }

            // 内容权重：包含关键词的句子权重更高
            if (sentence.contains("Redis") || sentence.contains("缓存") || sentence.contains("分布式")) {
                score += 5;
            }

            sentenceScores.add(new SentenceScore(sentence, score));
        }

        return sentenceScores;
    }

    /**
     * 截断句子
     *
     * @param sentence  句子
     * @param maxLength 最大长度
     * @return 截断后的句子
     */
    private static String truncateSentence(String sentence, int maxLength) {
        if (sentence.length() <= maxLength) {
            return sentence;
        }

        // 尝试在词语边界截断
        int endIndex = maxLength;
        while (endIndex > 0 && !Character.isWhitespace(sentence.charAt(endIndex - 1))) {
            endIndex--;
        }

        if (endIndex < maxLength * 0.8) {
            endIndex = maxLength;
        }

        return sentence.substring(0, endIndex) + "...";
    }

    /**
     * 判断是否为停用词
     *
     * @param word 词语
     * @return 是否为停用词
     */
    private static boolean isStopWord(String word) {
        Set<String> stopWords = new HashSet<>(Arrays.asList(
                "的", "了", "和", "与", "或", "是", "在", "有", "我", "他", "她", "它",
                "们", "这", "那", "你", "您", "他", "她", "它", "也", "就", "都", "要",
                "而", "很", "到", "说", "去", "你", "会", "着", "没有", "看", "好", "自己"
        ));
        return stopWords.contains(word);
    }

    /**
     * 判断是否为标点符号
     *
     * @param word 词语
     * @return 是否为标点符号
     */
    private static boolean isPunctuation(String word) {
        Pattern pattern = Pattern.compile("[\\p{Punct}\\s\\p{Cntrl}]");
        Matcher matcher = pattern.matcher(word);
        return matcher.matches();
    }
}
