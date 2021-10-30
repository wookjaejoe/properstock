import argparse
import os

import paramiko

import start_pub

arg_parser = argparse.ArgumentParser()
arg_parser.add_argument('--user', required=True)
arg_parser.add_argument('--pswd', required=True)
args = arg_parser.parse_args()
user = args.user
pswd = args.pswd


def print_hr():
    print('=' * 80)


def main():
    # 도커 이미지 제작
    registry = 'home.jowookjae.in:5000'
    container_name = 'finance-collactor-py-pub'
    version = start_pub.VERSION
    image_name = f'{registry}/{container_name}:{version}'
    run_cmd = f'docker build -t {image_name} .'

    print_hr()
    print(f'Run command at local: {run_cmd}')
    assert os.system(run_cmd) == 0
    print_hr()

    print_hr()
    run_cmd = f'docker push {image_name}'
    print(f'Run command at local: {run_cmd}')
    assert os.system(run_cmd) == 0
    print_hr()

    # 도커 컨테이너 실행
    host = 'home.jowookjae.in'
    ssh = paramiko.SSHClient()
    ssh.load_system_host_keys()
    ssh.connect(host, username=user, password=pswd)

    def execute(cmd: str):
        print(f'SSH EXECUTE COMMAND: {cmd}')
        _, so, se = ssh.exec_command(cmd)
        if so.channel.recv_exit_status() != 0:
            print('!! ERROR OCCURS !!')

        for line in iter(so.readline, ""):
            print(line, end="")

        for line in iter(se.readline, ""):
            print(line, end="")

    execute(f'docker stop {container_name}')
    execute(f'docker rm {container_name}')
    execute(f'docker rmi {container_name}')
    execute(f'docker pull {image_name}')
    execute(f'docker run -d --name {container_name} {image_name}')


if __name__ == '__main__':
    main()
