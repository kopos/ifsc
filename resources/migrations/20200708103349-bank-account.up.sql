-- :disable-transaction
CREATE TABLE `bnk_ifsc` (
    bank varchar(72),
    ifsc varchar(11),
    micr varchar(10),
    branch varchar(80),
    centre varchar(92),
    district varchar(165),
    city varchar(92),
    state varchar(111),
    address varchar(290),
    /*contact varchar(12),*/
    has_imps tinyint(1),
    has_neft tinyint(1),
    has_rtgs tinyint(1),
    has_upi tinyint(1)
);
--;;
CREATE INDEX IDX_U_IFSC ON `bnk_ifsc`(ifsc);
