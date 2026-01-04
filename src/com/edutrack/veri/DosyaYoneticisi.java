package com.edutrack.veri;

import com.edutrack.model.*;
import com.edutrack.util.Sabitler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javax.swing.JOptionPane;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


public class DosyaYoneticisi {
    private static final ReentrantLock LOCK = new ReentrantLock();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    
    private static final String KULLANICILAR_KAYNAK      = "/kullanicilar.json";
    private static final String PROJELER_KAYNAK         = "/projeler.json";
    private static final String BURS_KAYNAK             = "/burslar.json";
    private static final String ETKINLIK_KAYNAK         = "/etkinlikler.json";
    private static final String PROJE_BASVURU_KAYNAK    = "/proje_basvurular.json";
    private static final String BURS_BASVURU_KAYNAK     = "/burs_basvuru.json";
    private static final String ETKINLIK_BASVURU_KAYNAK = "/etkinlik_basvuru.json";

   
    private static Reader getReader(String externalPath, String resourcePath) throws IOException {
        File f = new File(externalPath);
        if (f.exists()) {
            return new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8);
        } else {
            InputStream is = DosyaYoneticisi.class.getResourceAsStream(resourcePath);
            if (is == null) {
                throw new FileNotFoundException("Classpath kaynağı bulunamadı: " + resourcePath);
            }
            return new InputStreamReader(is, StandardCharsets.UTF_8);
        }
    }

    
    private static void ensureExternalFileExists(String path) {
        File f = new File(path);
        if (!f.exists()) {
            try (Writer w = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8)) {
                w.write("[]");
            } catch (IOException ignored) {}
        }
    }

    
    public static <T> void yaz(String path, List<T> liste) {
        LOCK.lock();
        try {
            System.out.println("[DEBUG] yaz() çağrıldı: path=" + path + ", liste boyutu=" + liste.size());
            ensureExternalFileExists(path);
            try (Writer w = new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8)) {
                gson.toJson(liste, w);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Veri yazılamadı:\n" + e.getMessage(),
                Sabitler.BASLIK_DOSYA_HATASI,
                JOptionPane.ERROR_MESSAGE
            );
        } finally {
            LOCK.unlock();
        }
    }

    
    public static List<Kullanici> kullanicilariGetir() {
        LOCK.lock();
        try (Reader r = getReader(Sabitler.PATH_KULLANICILAR, KULLANICILAR_KAYNAK)) {
            Type type = new TypeToken<List<Kullanici>>() {}.getType();
            List<Kullanici> list = gson.fromJson(r, type);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Kullanıcı verisi okunamadı:\n" + e.getMessage(),
                Sabitler.BASLIK_DOSYA_HATASI,
                JOptionPane.ERROR_MESSAGE
            );
            return new ArrayList<>();
        } finally {
            LOCK.unlock();
        }
    }

    public static void kullaniciEkle(Kullanici k) {
        System.out.println("[DEBUG] kullaniciEkle çağrıldı: " + k);
        List<Kullanici> list = kullanicilariGetir();
        list.add(k);
        yaz(Sabitler.PATH_KULLANICILAR, list);
    }

    public static Kullanici girisKontrol(String ad, String sifre, String rol) {
        return kullanicilariGetir().stream()
            .filter(k -> k.getKullaniciAdi().equals(ad)
                      && k.getParola().equals(sifre)
                      && k.getRol().equalsIgnoreCase(rol))
            .findFirst().orElse(null);
    }

    
    public static List<Proje> projeleriGetir() {
        LOCK.lock();
        try (Reader r = getReader(Sabitler.PATH_PROJELER, PROJELER_KAYNAK)) {
            Type type = new TypeToken<List<Proje>>() {}.getType();
            List<Proje> list = gson.fromJson(r, type);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Proje verisi okunamadı:\n" + e.getMessage(),
                Sabitler.BASLIK_DOSYA_HATASI,
                JOptionPane.ERROR_MESSAGE
            );
            return new ArrayList<>();
        } finally {
            LOCK.unlock();
        }
    }

    public static void projeEkle(Proje p) {
        System.out.println("[DEBUG] projeEkle çağrıldı: id=" + p.getId());
        List<Proje> list = projeleriGetir();
        list.add(p);
        yaz(Sabitler.PATH_PROJELER, list);
    }

    public static void projeSil(String projeId) {
        System.out.println("[DEBUG] projeSil çağrıldı, silinecek id=" + projeId);
        List<Proje> list = projeleriGetir();
        boolean removed = list.removeIf(p -> p.getId().equals(projeId));
        System.out.println("[DEBUG] removeIf sonucu=" + removed + ", liste boyutu=" + list.size());
        yaz(Sabitler.PATH_PROJELER, list);
    }

    public static void projeGuncelle(Proje g) {
        System.out.println("[DEBUG] projeGuncelle çağrıldı, güncellenecek id=" + g.getId());
        List<Proje> list = projeleriGetir();
        boolean found = false;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(g.getId())) {
                list.set(i, g);
                found = true;
                System.out.println("[DEBUG] id bulundu, index=" + i);
                break;
            }
        }
        if (!found) {
            System.out.println("[WARN] güncellenecek proje bulunamadı: id=" + g.getId());
        }
        yaz(Sabitler.PATH_PROJELER, list);
    }

    
    public static List<ProjeBasvuru> projeBasvurulariniGetir() {
        LOCK.lock();
        try (Reader r = getReader(Sabitler.PATH_PROJE_BASVURULAR, PROJE_BASVURU_KAYNAK)) {
            Type type = new TypeToken<List<ProjeBasvuru>>() {}.getType();
            List<ProjeBasvuru> list = gson.fromJson(r, type);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Proje başvurusu verisi okunamadı:\n" + e.getMessage(),
                Sabitler.BASLIK_DOSYA_HATASI,
                JOptionPane.ERROR_MESSAGE
            );
            return new ArrayList<>();
        } finally {
            LOCK.unlock();
        }
    }

    public static void projeBasvuruEkle(ProjeBasvuru b) {
        List<ProjeBasvuru> list = projeBasvurulariniGetir();
        if (list.stream().noneMatch(ex -> ex.getKullaniciAdi().equals(b.getKullaniciAdi())
                                       && ex.getProjeId().equals(b.getProjeId()))) {
            list.add(b);
            yaz(Sabitler.PATH_PROJE_BASVURULAR, list);
        }
    }

    public static void projeBasvuruGuncelle(ProjeBasvuru g) {
        List<ProjeBasvuru> list = projeBasvurulariniGetir();
        for (int i = 0; i < list.size(); i++) {
            ProjeBasvuru ex = list.get(i);
            if (ex.getKullaniciAdi().equals(g.getKullaniciAdi())
             && ex.getProjeId().equals(g.getProjeId())) {
                list.set(i, g);
                break;
            }
        }
        yaz(Sabitler.PATH_PROJE_BASVURULAR, list);
    }

    public static void projeBasvuruSil(String kullaniciAdi, String projeId) {
        List<ProjeBasvuru> list = projeBasvurulariniGetir();
        list.removeIf(b -> b.getKullaniciAdi().equals(kullaniciAdi)
                         && b.getProjeId().equals(projeId));
        yaz(Sabitler.PATH_PROJE_BASVURULAR, list);
    }

    
    public static List<Burs> burslariGetir() {
        LOCK.lock();
        try (Reader r = getReader(Sabitler.PATH_BURSLAR, BURS_KAYNAK)) {
            Type type = new TypeToken<List<Burs>>() {}.getType();
            List<Burs> list = gson.fromJson(r, type);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Burs verisi okunamadı:\n" + e.getMessage(),
                Sabitler.BASLIK_DOSYA_HATASI,
                JOptionPane.ERROR_MESSAGE
            );
            return new ArrayList<>();
        } finally {
            LOCK.unlock();
        }
    }

    public static void bursEkle(Burs b) {
        System.out.println("[DEBUG] bursEkle çağrıldı: id=" + b.getId());
        List<Burs> list = burslariGetir();
        list.add(b);
        yaz(Sabitler.PATH_BURSLAR, list);
    }

    public static void bursGuncelle(Burs g) {
        System.out.println("[DEBUG] bursGuncelle çağrıldı: id=" + g.getId());
        List<Burs> list = burslariGetir();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(g.getId())) {
                list.set(i, g);
                System.out.println("[DEBUG] id bulundu, index=" + i);
                break;
            }
        }
        yaz(Sabitler.PATH_BURSLAR, list);
    }

    public static void bursSil(String bursId) {
        System.out.println("[DEBUG] bursSil çağrıldı, silinecek id=" + bursId);
        List<Burs> list = burslariGetir();
        boolean removed = list.removeIf(b -> b.getId().equals(bursId));
        System.out.println("[DEBUG] removeIf sonucu=" + removed + ", liste boyutu=" + list.size());
        yaz(Sabitler.PATH_BURSLAR, list);
    }

    
    public static List<BursBasvuru> bursBasvurulariniGetir() {
        LOCK.lock();
        try (Reader r = getReader(Sabitler.PATH_BURS_BASVURULAR, BURS_BASVURU_KAYNAK)) {
            Type type = new TypeToken<List<BursBasvuru>>() {}.getType();
            List<BursBasvuru> list = gson.fromJson(r, type);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Burs başvurusu verisi okunamadı:\n" + e.getMessage(),
                Sabitler.BASLIK_DOSYA_HATASI,
                JOptionPane.ERROR_MESSAGE
            );
            return new ArrayList<>();
        } finally {
            LOCK.unlock();
        }
    }

    public static void bursBasvuruEkle(BursBasvuru b) {
        List<BursBasvuru> list = bursBasvurulariniGetir();
        if (list.stream().noneMatch(ex -> ex.getKullaniciAdi().equals(b.getKullaniciAdi())
                                       && ex.getBursId().equals(b.getBursId()))) {
            list.add(b);
            yaz(Sabitler.PATH_BURS_BASVURULAR, list);
        }
    }

    public static void bursBasvuruGuncelle(BursBasvuru g) {
        List<BursBasvuru> list = bursBasvurulariniGetir();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getKullaniciAdi().equals(g.getKullaniciAdi())
             && list.get(i).getBursId().equals(g.getBursId())) {
                list.set(i, g);
                break;
            }
        }
        yaz(Sabitler.PATH_BURS_BASVURULAR, list);
    }

    public static void bursBasvuruSil(String kullaniciAdi, String bursId) {
        List<BursBasvuru> list = bursBasvurulariniGetir();
        list.removeIf(b -> b.getKullaniciAdi().equals(kullaniciAdi)
                         && b.getBursId().equals(bursId));
        yaz(Sabitler.PATH_BURS_BASVURULAR, list);
    }

    
    public static List<Etkinlik> etkinlikleriGetir() {
        LOCK.lock();
        try (Reader r = getReader(Sabitler.PATH_ETKINLIKER, ETKINLIK_KAYNAK)) {
            Type type = new TypeToken<List<Etkinlik>>() {}.getType();
            List<Etkinlik> list = gson.fromJson(r, type);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Etkinlik verisi okunamadı:\n" + e.getMessage(),
                Sabitler.BASLIK_DOSYA_HATASI,
                JOptionPane.ERROR_MESSAGE
            );
            return new ArrayList<>();
        } finally {
            LOCK.unlock();
        }
    }

    public static void etkinlikEkle(Etkinlik e) {
        System.out.println("[DEBUG] etkinlikEkle çağrıldı: id=" + e.getId());
        List<Etkinlik> list = etkinlikleriGetir();
        list.add(e);
        yaz(Sabitler.PATH_ETKINLIKER, list);
    }

    public static void etkinlikGuncelle(Etkinlik g) {
        System.out.println("[DEBUG] etkinlikGuncelle çağrıldı: id=" + g.getId());
        List<Etkinlik> list = etkinlikleriGetir();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getId().equals(g.getId())) {
                list.set(i, g);
                System.out.println("[DEBUG] id bulundu, index=" + i);
                break;
            }
        }
        yaz(Sabitler.PATH_ETKINLIKER, list);
    }

    public static void etkinlikSil(String etkinlikId) {
        System.out.println("[DEBUG] etkinlikSil çağrıldı, silinecek id=" + etkinlikId);
        List<Etkinlik> list = etkinlikleriGetir();
        boolean removed = list.removeIf(e -> e.getId().equals(etkinlikId));
        System.out.println("[DEBUG] removeIf sonucu=" + removed + ", liste boyutu=" + list.size());
        yaz(Sabitler.PATH_ETKINLIKER, list);
    }

    
    public static List<EtkinlikBasvuru> etkinlikBasvurulariniGetir() {
        LOCK.lock();
        try (Reader r = getReader(Sabitler.PATH_ETKINLIK_BASVURULAR, ETKINLIK_BASVURU_KAYNAK)) {
            Type type = new TypeToken<List<EtkinlikBasvuru>>() {}.getType();
            List<EtkinlikBasvuru> list = gson.fromJson(r, type);
            return list != null ? list : new ArrayList<>();
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "Etkinlik başvurusu verisi okunamadı:\n" + e.getMessage(),
                Sabitler.BASLIK_DOSYA_HATASI,
                JOptionPane.ERROR_MESSAGE
            );
            return new ArrayList<>();
        } finally {
            LOCK.unlock();
        }
    }

    public static void etkinlikBasvuruEkle(EtkinlikBasvuru b) {
        List<EtkinlikBasvuru> list = etkinlikBasvurulariniGetir();
        if (list.stream().noneMatch(ex -> ex.getKullaniciAdi().equals(b.getKullaniciAdi())
                                       && ex.getEtkinlikId().equals(b.getEtkinlikId()))) {
            list.add(b);
            yaz(Sabitler.PATH_ETKINLIK_BASVURULAR, list);
        }
    }

    public static void etkinlikBasvuruGuncelle(EtkinlikBasvuru g) {
        List<EtkinlikBasvuru> list = etkinlikBasvurulariniGetir();
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getKullaniciAdi().equals(g.getKullaniciAdi())
             && list.get(i).getEtkinlikId().equals(g.getEtkinlikId())) {
                list.set(i, g);
                break;
            }
        }
        yaz(Sabitler.PATH_ETKINLIK_BASVURULAR, list);
    }

    public static void etkinlikBasvuruSil(String kullaniciAdi, String etkinlikId) {
        List<EtkinlikBasvuru> list = etkinlikBasvurulariniGetir();
        list.removeIf(b -> b.getKullaniciAdi().equals(kullaniciAdi)
                         && b.getEtkinlikId().equals(etkinlikId));
        yaz(Sabitler.PATH_ETKINLIK_BASVURULAR, list);
    }

}