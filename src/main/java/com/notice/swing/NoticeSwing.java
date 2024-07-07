package com.notice.swing;

import cn.hutool.core.util.StrUtil;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;

import javax.swing.*;
import java.awt.*;
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


    public NoticeSwing() {
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
        //窗体关闭，直接退出程序
        closeButton.addActionListener(e -> System.exit(0));
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
        frame.setContentPane(new NoticeSwing().noticeSwing);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        noticeSwing = new JPanel();
        noticeSwing.setLayout(new GridLayoutManager(5, 5, new Insets(0, 0, 0, 0), -1, -1));
        closeButton = new JButton();
        closeButton.setText("关闭");
        noticeSwing.add(closeButton, new GridConstraints(3, 3, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("关键字");
        noticeSwing.add(label1, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exportButton = new JButton();
        exportButton.setText("导出");
        noticeSwing.add(exportButton, new GridConstraints(3, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        searchWord = new JTextField();
        searchWord.setText("");
        noticeSwing.add(searchWord, new GridConstraints(1, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("开始时间");
        noticeSwing.add(label2, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        startDate = new JTextField();
        startDate.setText("");
        noticeSwing.add(startDate, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label3 = new JLabel();
        label3.setText("结束时间");
        noticeSwing.add(label3, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        endDate = new JTextField();
        endDate.setText("");
        noticeSwing.add(endDate, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("最大页数");
        noticeSwing.add(label4, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        maxPage = new JTextField();
        maxPage.setDropMode(DropMode.USE_SELECTION);
        maxPage.setName("");
        maxPage.setText("10");
        maxPage.setToolTipText("111");
        noticeSwing.add(maxPage, new GridConstraints(1, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("导出路径");
        noticeSwing.add(label5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        choose = new JButton();
        choose.setText("选择目录");
        noticeSwing.add(choose, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exportPath = new JTextField();
        exportPath.setEditable(false);
        exportPath.setText("");
        noticeSwing.add(exportPath, new GridConstraints(2, 2, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        progressBar = new JProgressBar();
        progressBar.setForeground(new Color(-15921435));
        noticeSwing.add(progressBar, new GridConstraints(4, 0, 1, 5, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return noticeSwing;
    }
}
