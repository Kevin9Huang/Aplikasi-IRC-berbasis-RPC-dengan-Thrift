==========Tugas Aplikasi IRC berbasis RPC dengan menggunakan Thrift===========

--------------------------------Client--------------------------------
1. Ketika pertama kali dijalankan, program akan meminta no port yang akan menjalin komunikasi dengan server. Jika pada saat tersebut tidak ada server
pada port yang dimasukkan aktif, maka program akan mengeluarkan pesan exception.
2. Pada Client handler yang akan memproses setiap message yang dikirim oleh
server kepada client.
3. Pada Client terhadap thread yang akan melakukan pengecekan terhadap server,
jika server secara tiba-tiba terputus, maka Client akan mencoba melakukan
pengoneksian ulang. Untuk sekarang client yang melakukan reconnect akan masih
menjadi client baru(tanpa nick dan channel).
4. Untuk setiap client yang baru melakukan koneksi terhadap server, client
akan menjalankan method RequestID() yang meminta ID unik client dari server.

--------------------------------Server--------------------------------
1. Untuk setiap client yang melakukan koneksi terhadap server, server akan
membuat 1 processor untuk melayani client tersebut dan membuat 1 object kelas
MessageServiceClient yang menyimpan data-data dari client tersebut.
2. Untuk setiap permintaan /JOIN dari client, akan dilakukan pengecekan
terhadap AllChannel yang ada pada server. Jika belum ada channel tersebut,
server akan menambah server tersebut kedalam list AllChannel. Pada aplikasi
ini belum dilakukan penghapusan sebuah channel jika sudah tidak ada user yang
berada didalam Channel tersebut.
3. Untuk setiap message yang dikirim kepada server akan dimasukkan terlebih
dahulu kedalam sebuah queue, untuk kemudiannya akan diproses oleh kelas
MessageDistributor secara satu per satu.

Keterangan:
1. Masiah terdapat kesalahan pada saat menjalankan client pertama,untuk
sementara hipotesis kami adalah kesalahan pada pembukaan/alokasi soket oleh 
client. Client yang dilakukan pengetesan sebaiknya dicoba menggunakan client 1
dan 2