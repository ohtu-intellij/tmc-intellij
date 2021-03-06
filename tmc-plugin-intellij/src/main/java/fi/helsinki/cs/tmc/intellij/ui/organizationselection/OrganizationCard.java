package fi.helsinki.cs.tmc.intellij.ui.organizationselection;

import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import fi.helsinki.cs.tmc.core.domain.Organization;
import fi.helsinki.cs.tmc.intellij.holders.TmcSettingsManager;

import javax.swing.*;
import java.awt.*;
import java.net.MalformedURLException;
import java.net.URL;

public class OrganizationCard extends JPanel {
    private JLabel logo;
    private JLabel organizationName;
    private JLabel organizationSlug;
    private JScrollPane infoScrollPane;
    private JTextArea organizationInformation;

    private final Organization organization;
    private ImageIcon image;
    private JBList parent;

    public OrganizationCard(Organization organization, JBList parent) {
        this.initComponents();

        this.organization = organization;
        this.parent = parent;

        this.organizationName.setText(organization.getName());
        String information = organization.getInformation();
        if (information.length() > 188) {
            information = information.substring(0, 187) + "...";
        }
        this.organizationInformation.setText(information);
        this.organizationSlug.setText("/" + organization.getSlug());

        setLogo();
    }

    private void setLogo() {
        setLogo(logoUrl("placeholderLogo.png"));
        final String logoPath = organization.getLogoPath();
        if(!logoPath.contains("missing")) {
            new Thread(() -> {
                setLogo(logoUrl(logoPath));
                this.parent.repaint();
            }).start();
        }
    }

    private void setLogo(URL logoUrl) {
        this.image = new ImageIcon(logoUrl);
        this.image.setImage(this.image.getImage().getScaledInstance(49, 49, Image.SCALE_SMOOTH));
        this.logo.setIcon(this.image);
    }

    public Organization getOrganization() {
        return this.organization;
    }

    private URL logoUrl(String path) {
        final String address = TmcSettingsManager.get().getServerAddress();
        String url;
        if (address.charAt(address.length() - 1) == '/' && path.charAt(0) == '/') {
            url = address.substring(0, address.length() - 1) + path;
        } else {
            url = address + path;
        }

        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            return null;
        }
    }

    public void setColors(Color foreground, Color background) {
        this.setBackground(background);
        this.organizationName.setForeground(foreground);
        this.organizationSlug.setForeground(foreground);
        this.infoScrollPane.setBackground(background);
        this.infoScrollPane.setForeground(foreground);
        this.organizationInformation.setForeground(foreground);
        this.organizationInformation.setBackground(background);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        logo = new JLabel();
        organizationName = new JLabel();
        organizationSlug = new JLabel();
        organizationInformation = new JTextArea();
        infoScrollPane = new JBScrollPane(organizationInformation);

        setBackground(new Color(255, 255, 255));
        setBorder(
                BorderFactory.createMatteBorder(
                        1,
                        10,
                        1,
                        10,
                        new JBColor(new Color(242, 241, 240), new Color(242, 241, 24))));
        setMaximumSize(new Dimension(332, 73));
        setMinimumSize(new Dimension(332, 73));

        logo.setMaximumSize(new Dimension(49, 49));
        logo.setMinimumSize(new Dimension(49, 49));
        logo.setPreferredSize(new Dimension(49, 49));

        organizationName.setFont(new Font("Ubuntu", 1, 18)); // NOI18N

        organizationSlug.setForeground(new Color(150, 150, 150));

        infoScrollPane.setBackground(new Color(255, 255, 255));
        infoScrollPane.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        infoScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        infoScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        infoScrollPane.setViewportBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        infoScrollPane.setHorizontalScrollBar(null);
        infoScrollPane.setPreferredSize(new Dimension(106, 30));

        organizationInformation.setEditable(false);
        organizationInformation.setBackground(new Color(255, 255, 255));
        organizationInformation.setLineWrap(true);
        organizationInformation.setWrapStyleWord(true);
        organizationInformation.setAutoscrolls(false);
        organizationInformation.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        organizationInformation.setMaximumSize(null);
        infoScrollPane.setViewportView(organizationInformation);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(
                                                logo,
                                                GroupLayout.PREFERRED_SIZE,
                                                GroupLayout.DEFAULT_SIZE,
                                                GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(
                                                layout.createParallelGroup(
                                                                GroupLayout.Alignment.LEADING)
                                                        .addGroup(
                                                                layout.createSequentialGroup()
                                                                        .addComponent(
                                                                                organizationName,
                                                                                GroupLayout
                                                                                        .PREFERRED_SIZE,
                                                                                200,
                                                                                GroupLayout
                                                                                        .PREFERRED_SIZE)
                                                                        .addPreferredGap(
                                                                                LayoutStyle
                                                                                        .ComponentPlacement
                                                                                        .RELATED,
                                                                                GroupLayout
                                                                                        .DEFAULT_SIZE,
                                                                                Short.MAX_VALUE)
                                                                        .addComponent(
                                                                                organizationSlug))
                                                        .addComponent(
                                                                infoScrollPane,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                Short.MAX_VALUE))
                                        .addContainerGap()));
        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addGroup(
                                                layout.createParallelGroup(
                                                                GroupLayout.Alignment.LEADING)
                                                        .addComponent(
                                                                logo,
                                                                GroupLayout.PREFERRED_SIZE,
                                                                GroupLayout.DEFAULT_SIZE,
                                                                GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(
                                                                layout.createSequentialGroup()
                                                                        .addGroup(
                                                                                layout.createParallelGroup(
                                                                                                GroupLayout
                                                                                                        .Alignment
                                                                                                        .LEADING)
                                                                                        .addComponent(
                                                                                                organizationSlug)
                                                                                        .addComponent(
                                                                                                organizationName))
                                                                        .addPreferredGap(
                                                                                LayoutStyle
                                                                                        .ComponentPlacement
                                                                                        .UNRELATED)
                                                                        .addComponent(
                                                                                infoScrollPane,
                                                                                GroupLayout
                                                                                        .PREFERRED_SIZE,
                                                                                40,
                                                                                GroupLayout
                                                                                        .PREFERRED_SIZE)))
                                        .addContainerGap(
                                                GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
    } // </editor-fold>//GEN-END:initComponents
}
