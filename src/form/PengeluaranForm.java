package form;

import controller.PengeluaranController;
import controller.AuthController;
import java.awt.*;
import java.time.LocalDate;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Pengeluaran;
import util.UIUtils;

public class PengeluaranForm extends JPanel {
    private final PengeluaranController c = new PengeluaranController();
    private final DefaultTableModel model = new DefaultTableModel(new String[]{"ID","Tanggal","Kategori","Nominal","Keterangan"},0){ public boolean isCellEditable(int r,int c){return false;} };
    private final JTable table = new JTable(model);
    private final JLabel total = new JLabel("Total: Rp 0");
    private final JTextField dari = UIUtils.textField(10), sampai = UIUtils.textField(10);
    public PengeluaranForm(){ setLayout(new BorderLayout()); JPanel p=UIUtils.page("Pengeluaran"); dari.setText(LocalDate.now().withDayOfMonth(1).toString()); sampai.setText(LocalDate.now().toString()); JButton filter=UIUtils.button("FILTER",UIUtils.BLUE), add=UIUtils.button("TAMBAH",UIUtils.GREEN), del=UIUtils.button("HAPUS",UIUtils.RED); p.add(UIUtils.toolbar(UIUtils.formLabel("Dari"),dari,UIUtils.formLabel("Sampai"),sampai,filter,add,del),BorderLayout.NORTH); JPanel card=UIUtils.card(); card.add(UIUtils.tableScroll(table)); p.add(card); total.setFont(UIUtils.FONT_H2); JPanel foot=new JPanel(new FlowLayout(FlowLayout.RIGHT)); foot.setOpaque(false); foot.add(total); p.add(foot,BorderLayout.SOUTH); add(p); table.removeColumn(table.getColumnModel().getColumn(0)); filter.addActionListener(e->load()); add.addActionListener(e->dialog()); del.addActionListener(e->delete()); load(); }
    private void load(){ model.setRowCount(0); double sum=0; for(Pengeluaran x:c.getByPeriode(LocalDate.parse(dari.getText()), LocalDate.parse(sampai.getText()))){ sum+=x.getNominal(); model.addRow(new Object[]{x.getId(),x.getTanggal(),x.getNamaKategori(),UIUtils.rupiah(x.getNominal()),x.getKeterangan()}); } total.setText("Total: "+UIUtils.rupiah(sum)); }
    private void dialog(){ JComboBox<UIUtils.Option> kat=UIUtils.comboBox(c.getKategori().toArray(new UIUtils.Option[0])); JTextField tgl=UIUtils.textField(12), nominal=UIUtils.textField(14), siswa=UIUtils.textField(8); JTextArea ket=new JTextArea(4,24); ket.setFont(UIUtils.FONT_PLAIN); tgl.setText(LocalDate.now().toString()); JPanel p=new JPanel(new GridLayout(0,1,6,6)); p.add(UIUtils.formLabel("Tanggal (YYYY-MM-DD)"));p.add(tgl); p.add(UIUtils.formLabel("Kategori"));p.add(kat);  p.add(UIUtils.formLabel("Nominal"));p.add(nominal); p.add(UIUtils.formLabel("Keterangan"));p.add(new JScrollPane(ket)); if(JOptionPane.showConfirmDialog(this,p,"Tambah Pengeluaran",JOptionPane.OK_CANCEL_OPTION)==JOptionPane.OK_OPTION){ UIUtils.Option ko=(UIUtils.Option)kat.getSelectedItem(); int uid=AuthController.getCurrentUser()==null?0:AuthController.getCurrentUser().getId(); Pengeluaran val=new Pengeluaran(0,LocalDate.parse(tgl.getText()),ko.id,"",UIUtils.parseNumber(nominal.getText()),ket.getText(),uid); if(c.save(val)){UIUtils.showSuccess(this,"Data tersimpan");load();} else UIUtils.showError(this,"Gagal menyimpan"); } }
    private void delete(){ int r=table.getSelectedRow(); if(r<0)return; int m=table.convertRowIndexToModel(r); int id=(int)model.getValueAt(m,0); if(UIUtils.showConfirm(this,"Hapus transaksi?")){ c.delete(id); load(); } }
}
