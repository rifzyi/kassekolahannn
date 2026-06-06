package form;

import controller.AuthController;
import controller.TabunganController;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.Siswa;
import model.Tabungan;
import util.UIUtils;

public class TabunganForm extends JPanel { private final TabunganController c=new TabunganController(); private Siswa selected; private final JLabel info=new JLabel("Belum ada siswa dipilih"); private final DefaultTableModel model=new DefaultTableModel(new String[]{"Tanggal","Jenis","Nominal","Saldo Akhir","Keterangan"},0); private final JTable table=new JTable(model); private final JTextField q=UIUtils.textField(22), nominal=UIUtils.textField(14); private final JTextArea ket=new JTextArea(3,30); private final JToggleButton setor=new JToggleButton("SETOR",true), tarik=new JToggleButton("TARIK"); public TabunganForm(){ setLayout(new BorderLayout()); JPanel p=UIUtils.page("Tabungan"); JButton cari=UIUtils.button("CARI",UIUtils.BLUE), proses=UIUtils.button("PROSES",UIUtils.GREEN); ButtonGroup bg=new ButtonGroup(); bg.add(setor); bg.add(tarik); JPanel top=UIUtils.card(); top.setLayout(new GridLayout(0,1,8,8)); top.add(UIUtils.toolbar(UIUtils.formLabel("Cari Siswa"),q,cari)); info.setFont(UIUtils.FONT_H2); top.add(info); top.add(UIUtils.toolbar(setor,tarik,UIUtils.formLabel("Nominal"),nominal,proses)); ket.setFont(UIUtils.FONT_PLAIN); top.add(new JScrollPane(ket)); JPanel center=new JPanel(new BorderLayout(0,12)); center.setOpaque(false); center.add(top,BorderLayout.NORTH); JPanel card=UIUtils.card(); card.add(UIUtils.tableScroll(table)); center.add(card); p.add(center); add(p); cari.addActionListener(e->search()); proses.addActionListener(e->process()); }
private void search(){ List<Siswa> list=c.searchSiswa(q.getText()); if(list.isEmpty()){UIUtils.showError(this,"Siswa tidak ditemukan");return;} selected=list.get(0); load(); }
private void load(){ model.setRowCount(0); double saldo=c.getSaldoSiswa(selected.getId()); info.setText(selected.getNis()+" - "+selected.getNamaSiswa()+" | Saldo: "+UIUtils.rupiah(saldo)); for(Tabungan t:c.getMutasiSiswa(selected.getId())) model.addRow(new Object[]{t.getTanggal(),t.getJenis(),UIUtils.rupiah(t.getNominal()),UIUtils.rupiah(t.getSaldoAkhir()),t.getKeterangan()}); }
private void process(){ if(selected==null){UIUtils.showError(this,"Pilih siswa dahulu");return;} double n=UIUtils.parseNumber(nominal.getText()); int uid=AuthController.getCurrentUser()==null?0:AuthController.getCurrentUser().getId(); boolean ok=setor.isSelected()?c.setor(selected.getId(),n,ket.getText(),uid):c.tarik(selected.getId(),n,ket.getText(),uid); if(ok){UIUtils.showSuccess(this,"Transaksi berhasil"); load();} else UIUtils.showError(this,"Transaksi gagal / saldo tidak cukup"); }
}
