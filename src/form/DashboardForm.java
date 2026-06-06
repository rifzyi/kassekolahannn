package form;

import controller.DashboardController;
import java.awt.*;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import util.UIUtils;

public class DashboardForm extends JPanel {
    private final DashboardController c = new DashboardController();
    private final JPanel saldo = UIUtils.createKPICard("💰 Saldo Kas", "Rp 0", UIUtils.BLUE);
    private final JPanel masuk = UIUtils.createKPICard("📈 Pemasukan Bulan Ini", "Rp 0", UIUtils.GREEN);
    private final JPanel keluar = UIUtils.createKPICard("📉 Pengeluaran Bulan Ini", "Rp 0", UIUtils.RED);
    private final JPanel tunggak = UIUtils.createKPICard("⚠️ Siswa Menunggak", "0", UIUtils.ORANGE);
    private final ChartPanel chart = new ChartPanel();
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"Tanggal","Jenis","Keterangan","Nominal"},0);
    public DashboardForm(){ setLayout(new BorderLayout()); JPanel page=UIUtils.page("Dashboard"); JPanel top=new JPanel(new BorderLayout()); top.setOpaque(false); JButton refresh=UIUtils.button("Refresh", UIUtils.BLUE); refresh.addActionListener(e->load()); top.add(refresh,BorderLayout.EAST); JPanel center=new JPanel(new BorderLayout(0,14)); center.setOpaque(false); JPanel kpi=new JPanel(new GridLayout(1,4,12,0)); kpi.setOpaque(false); kpi.add(saldo); kpi.add(masuk); kpi.add(keluar); kpi.add(tunggak); center.add(kpi,BorderLayout.NORTH); JPanel ch=UIUtils.card(); ch.add(chart); center.add(ch,BorderLayout.CENTER); JTable table=new JTable(model); JPanel tbl=UIUtils.card(); tbl.add(UIUtils.tableScroll(table)); center.add(tbl,BorderLayout.SOUTH); page.add(top,BorderLayout.NORTH); page.add(center); add(page); load(); }
    private void load(){ UIUtils.updateKPICard(saldo,UIUtils.rupiah(c.getSaldoKas())); UIUtils.updateKPICard(masuk,UIUtils.rupiah(c.getPemasukanBulanIni())); UIUtils.updateKPICard(keluar,UIUtils.rupiah(c.getPengeluaranBulanIni())); UIUtils.updateKPICard(tunggak,String.valueOf(c.getJumlahSiswaTunggak())); chart.setData(c.getData6BulanTerakhir()); model.setRowCount(0); for(String[] r:c.getTransaksiTerbaru(10)) model.addRow(r); }
    private static class ChartPanel extends JPanel { private Map<String,double[]> data; ChartPanel(){ setPreferredSize(new Dimension(600,260)); setBackground(UIUtils.WHITE); } void setData(Map<String,double[]> d){ data=d; repaint(); } @Override protected void paintComponent(Graphics g){ super.paintComponent(g); if(data==null)return; Graphics2D g2=(Graphics2D)g; g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON); int w=getWidth(), h=getHeight(), left=45, bottom=h-45; double max=1; for(double[] v:data.values()) max=Math.max(max,Math.max(v[0],v[1])); int i=0, group=(w-left-25)/Math.max(1,data.size()); g2.setFont(UIUtils.FONT_SMALL); g2.setColor(UIUtils.BLUE); g2.fillRect(w-170,15,12,12); g2.setColor(UIUtils.TEXT_DARK); g2.drawString("Pemasukan",w-152,26); g2.setColor(UIUtils.RED); g2.fillRect(w-80,15,12,12); g2.setColor(UIUtils.TEXT_DARK); g2.drawString("Pengeluaran",w-62,26); for(Map.Entry<String,double[]> e:data.entrySet()){ int x=left+i*group+12; int bh1=(int)(e.getValue()[0]/max*(h-90)); int bh2=(int)(e.getValue()[1]/max*(h-90)); g2.setColor(UIUtils.BLUE); g2.fillRoundRect(x,bottom-bh1,22,bh1,6,6); g2.setColor(UIUtils.RED); g2.fillRoundRect(x+28,bottom-bh2,22,bh2,6,6); g2.setColor(UIUtils.TEXT_GRAY); g2.drawString(e.getKey(),x,bottom+18); i++; } } }
}
