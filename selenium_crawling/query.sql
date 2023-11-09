create table airplaneDepart(
    flightName VARCHAR2(50),
    depDay date,
    depTime timestamp,               
    fullDeparture VARCHAR2(200),
    depCode varchar2(50),
    depName varchar2(200),
    fullArrival varchar2(200),
    arrCode varchar2(50),
    arrName varchar2(200)
);

create table airplaneArrive(
    flightName VARCHAR2(50),
    arrDay date,
    arrTime timestamp,               
    fullDeparture VARCHAR2(200),
    depCode varchar2(50),
    depName varchar2(200),
    fullArrival varchar2(200),
    arrCode varchar2(50),
    arrName varchar2(200)
);

create table airplaneSchedule(
    flightName VARCHAR2(20),
    depDay date,
    arrDay date,
    depTime timestamp,        
    arrTime timestamp, 
    flightTime interval day to second,       
    fullDeparture VARCHAR2(200),
    depCode varchar2(50),
    depName varchar2(200),
    fullArrival varchar2(200),
    arrCode varchar2(50),
    arrName varchar2(200),
    depRegionCode number(4),        
    arrRegionCode number(4),        
    primary key(flightName, depDay)       
);
alter table airplaneSchedule add constraint fk_depRegionCode foreign key (depRegionCode) references regionCode(code);
alter table airplaneSchedule add constraint fk_arrRegionCode foreign key (arrRegionCode) references regionCode(code);
-------------------------------------------------------------------------------------------------------------
--���̺� ���� ����
INSERT INTO airplaneSchedule(flightName, depDay, depTime,fullDeparture, depCode, depName, fullArrival, arrCode, arrName) 
    SELECT flightName, depDay, depTime, fullDeparture, depCode, depName, fullArrival, arrCode, arrName FROM airplaneDepart;

--�ߺ��� �����Ͱ� �ִ��� Ȯ��(�⺻Ű Ȱ��)
select (flightName||depDay) from airplaneDepart group by (flightName||depDay) having count((flightName||depDay))>=2;

--arrTime ������Ʈ    
MERGE INTO airplaneSchedule a
USING airplaneArrive b
   ON (a.depday = b.arrday 
        and a.flightname = b.flightname)
 WHEN MATCHED THEN
      UPDATE 
         SET a.arrTime = b.arrTime;
         
--arrDay ������Ʈ
MERGE INTO airplaneSchedule a
USING airplaneArrive b
   ON (a.depday = b.arrday 
        and a.flightname = b.flightname)
 WHEN MATCHED THEN
      UPDATE 
         SET a.arrDay = b.arrDay;
    
--flightTime ����Ͽ� ������Ʈ
update airplaneSchedule 
    set flightTime = (arrTime-depTime);

select count(*) from airplaneDepart;
select count(*) from airplaneArrive;
select count(*) from airplaneSchedule;		--13373��

--������ ����
select count(*) from airplaneSchedule where flightTime is null;
delete from airplaneSchedule where flightTime is null;
commit;

--distinct�� ���/������, �װ��� �˻�
select distinct(depName) from airplaneSchedule order by depName;	--48��
select distinct(arrName) from airplaneSchedule order by arrName;		--47��
select distinct(depName||arrName) from airplaneSchedule order by 1;	--98��

--������ �����ϴ� �װ��� ������ �� ������Ʈ. 
select count(*) from airplaneSchedule where flightTime<'-00 00:00:01.000000';
update airplaneSchedule set arrday = arrday+1 where flightTime<'-00 00:00:01.000000';
update airplaneSchedule set arrtime=arrtime+1 where flightTime<'-00 00:00:01.000000';
commit;

--3/1������, ī�̷� �װ��� ����
select * from airplaneSchedule where arrday = '23/03/01';
delete from airplaneSchedule where arrday = '23/03/01';
delete from airplaneSchedule where arrname = 'ī�̷�';

--�̸� ����
select count(*) from airplaneSchedule where depname like '%������丣%';
update airplaneSchedule set depname = '��ī��Ÿ' where depname like '%��ī����%';
update airplaneSchedule set arrname = '��ī��Ÿ' where arrname like '%��ī����%';
update airplaneSchedule set depname = '�����Ÿ��' where depname like '%������丣%';
update airplaneSchedule set arrname = '�����Ÿ��' where arrname like '%������丣%';
update airplaneSchedule set depname = '��¡' where depname like '����';
update airplaneSchedule set arrname = '��¡' where arrname like '����';
update airplaneSchedule set depname = 'û��' where depname like '����';
update airplaneSchedule set arrname = 'û��' where arrname like '����';
update airplaneSchedule set depname = '����' where depname like '��õ';
update airplaneSchedule set arrname = '����' where arrname like '��õ';
update airplaneSchedule set depname = '����' where depname like '����';
update airplaneSchedule set arrname = '����' where arrname like '����';
update airplaneSchedule set depname = 'â��' where depname like '����';
update airplaneSchedule set arrname = 'â��' where arrname like '����';
update airplaneSchedule set depname = 'Ÿ�̺���' where depname like '%Ÿ�̿�%';
update airplaneSchedule set arrname = 'Ÿ�̺���' where arrname like '%Ÿ�̿�%';
commit;

--�����ڵ� �Է�
select count(*) from airplaneSchedule where depname in ('����', '����', '�뱸', '����', '��õ', '����', 'û��' );
select count(*) from airplaneSchedule where arrname in ('����', '����', '�뱸', '����', '��õ', '����', 'û��' );
update airplaneSchedule set depregioncode=1 where depname in ('����', '����', '�뱸', '����', '��õ', '����', 'û��');
update airplaneSchedule set arrregioncode=1 where arrname in ('����', '����', '�뱸', '����', '��õ', '����', 'û��');
update airplaneSchedule set depregioncode=2 where depname in ('������', '�����', '��¡', '����/����Ÿ', '�̾���Ű', '����¡', '������', 'û��', '����', '����', '��Ű����', 'â��', 'Ÿ�̺���', 'Ǫ��', '�ϳ״�', '�Ͼ��', '������', 'ȫ��', '�����ī');
update airplaneSchedule set arrregioncode=2 where arrname in ('������', '�����', '��¡', '����/����Ÿ', '�̾���Ű', '����¡', '������', 'û��', '����', '����', '��Ű����', 'â��', 'Ÿ�̺���', 'Ǫ��', '�ϳ״�', '�Ͼ��', '������', 'ȫ��', '�����ī');
update airplaneSchedule set depregioncode=3 where depname in ('�ٳ�', '����', '���Ҷ�', '����', '��ī��Ÿ', '�̰�����', 'Ǫ��', '������', '�ϳ���', 'ȣ���');
update airplaneSchedule set arrregioncode=3 where arrname in ('�ٳ�', '����', '���Ҷ�', '����', '��ī��Ÿ', '�̰�����', 'Ǫ��', '������', '�ϳ���', 'ȣ���');
update airplaneSchedule set depregioncode=4 where depname in ('�����Ÿ��','�˸�Ƽ','Ÿ����Ʈ');
update airplaneSchedule set arrregioncode=4 where arrname in ('�����Ÿ��','�˸�Ƽ','Ÿ����Ʈ');
update airplaneSchedule set depregioncode=5 where depname in ('���������', '�θ�', '�ٸ����γ�', '�̽�ź��', '�ĸ�', '����ũǪ��Ʈ');
update airplaneSchedule set arrregioncode=5 where arrname in ('���������', '�θ�', '�ٸ����γ�', '�̽�ź��', '�ĸ�', '����ũǪ��Ʈ');
update airplaneSchedule set depregioncode=6 where depname in ('����', '�ν���������', '�������ý���', '�þ�Ʋ' ,'ȣ����');
update airplaneSchedule set arrregioncode=6 where arrname in ('����', '�ν���������', '�������ý���', '�þ�Ʋ' ,'ȣ����');
update airplaneSchedule set depregioncode=7 where depname in ('������', '�õ��');
update airplaneSchedule set arrregioncode=7 where arrname in ('������', '�õ��');

select * from airplaneSchedule where depregioncode is null;
select * from airplaneSchedule where arrregioncode is null;

commit;
-------------------------------------------------------------------------------------------------------------------
create table regionCode(
    region varchar2(100),
    code number primary key
);
insert into regionCode values('�ѱ�', 1);
insert into regionCode values('���Ͼƽþ�', 2);
insert into regionCode values('�����ƽþ�/�����ƽþ�', 3);
insert into regionCode values('����/�߾Ӿƽþ�', 4);
insert into regionCode values('����', 5);
insert into regionCode values('����(�̱�,ĳ����,�߳���)', 6);
insert into regionCode values('�����/������/�ʸ���', 7);
insert into regionCode values('�ߵ�/������ī', 8);

commit;