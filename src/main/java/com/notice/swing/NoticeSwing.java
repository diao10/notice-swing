package com.notice.swing;

import cn.hutool.core.util.StrUtil;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @author diaoyn
 * @ClassName NoticeSwing
 * @Date 2024/7/1 16:45
 */
public class NoticeSwing {
    private JPanel noticeSwing;
    private JButton closeButton;
    private JButton exportButton;
    private JTextField searchWord;
    private JTextField startDate;
    private JTextField endDate;
    private JButton choose;
    private JTextField exportPath;
    private JTextField maxPage;
    private JProgressBar progressBar;


    public NoticeSwing(JFrame frame) {
        //导出按钮
        exportButton.addActionListener(e -> {
            if (StrUtil.isBlank(exportPath.getText())) {
                JOptionPane.showMessageDialog(noticeSwing, "请输入导出路径");
                return;
            }
            progressBar.setValue(0);
            CcbFundListener.exportNotice(startDate.getText(), endDate.getText(), searchWord.getText(), maxPage.getText(), exportPath.getText(), progressBar);
            progressBar.setValue(100);
            JOptionPane.showMessageDialog(noticeSwing, "导出成功");
        });
        //窗体关闭
        closeButton.addActionListener(e -> frame.dispose());
        //选择框
        choose.addActionListener(e -> {
            // 创建文件选择器,设置当前目录为当前目录
            JFileChooser fileChooser = new JFileChooser(".");
            //只有文件夹可以选择
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnVal = fileChooser.showDialog(null, "选择文件夹");
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                //选择的路径
                System.out.println(fileChooser.getSelectedFile().getPath());
                exportPath.setText(fileChooser.getSelectedFile().getPath());
            }
        });
        //maxPage监听
        maxPage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                String key = "0123456789";
                if (key.indexOf(e.getKeyChar()) < 0) {
                    e.consume();//如果不是数字则取消
                }
            }
        });
    }


    public static void start() {
        JFrame frame = new JFrame("NoticeSwing");
        frame.setContentPane(new NoticeSwing(frame).noticeSwing);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
