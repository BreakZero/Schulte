# 使用说明

本文档说明如何在本地启动舒尔特训练 RESTful API 服务，并调用用户管理与训练记录接口。

测试覆盖注册 ID 登录、受保护接口、游客记录、训练记录关联、幂等提交、进步状态计算和清空记录。

## 通用约定

请求和响应均使用 JSON：

```http
Content-Type: application/json
Accept: application/json
```

登录后，请在需要认证的接口中携带：

```http
Authorization: Bearer <accessToken>
```

错误响应格式：

```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "请求参数不合法"
  },
  "requestId": "req_local"
}
```

## 注册并登录

```bash
curl -s -X POST http://127.0.0.1:8080/api/v1/auth/register \
  -H 'Content-Type: application/json' \
  -d '{
    "password": "StrongPassword123",
    "nickname": "小明",
    "gender": "MALE",
    "acceptedTerms": true,
    "device": {
      "deviceName": "iPhone",
      "platform": "IOS"
    }
  }'
```

响应中的 `registrationId` 是系统生成的唯一注册 ID，例如 `SQT-100001`。`accessToken` 用于访问受保护接口，`refreshToken` 用于刷新登录状态。

## 登录

```bash
curl -s -X POST http://127.0.0.1:8080/api/v1/auth/login \
  -H 'Content-Type: application/json' \
  -d '{
    "registrationId": "SQT-100001",
    "password": "StrongPassword123",
    "device": {
      "deviceName": "iPhone",
      "platform": "IOS"
    }
  }'
```

当前版本使用注册 ID 登录，不依赖邮箱。注册 ID 不允许用户修改。

## 获取当前用户

```bash
curl -s http://127.0.0.1:8080/api/v1/me \
  -H 'Authorization: Bearer <accessToken>'
```

## 更新用户资料

```bash
curl -s -X PATCH http://127.0.0.1:8080/api/v1/me \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <accessToken>' \
  -d '{
    "nickname": "新的昵称",
    "gender": "UNDISCLOSED",
    "avatarUrl": "https://example.com/avatar.png"
  }'
```

可选性别值：`MALE`、`FEMALE`、`UNDISCLOSED`。注册 ID、段位、胜率不可通过资料接口修改。

## 获取个人中心资料

```bash
curl -s http://127.0.0.1:8080/api/v1/me/profile \
  -H 'Authorization: Bearer <accessToken>'
```

该接口返回账号资料、训练统计和竞技资料占位。当前 PK 未上线，段位固定展示「未定级」，胜率展示「暂无对战数据」。

## 创建游客训练记录

未登录用户仍可正常训练。客户端应为当前设备生成并保存一个稳定的 `guestId`。

```bash
curl -s -X POST http://127.0.0.1:8080/api/v1/guest/training-records \
  -H 'Content-Type: application/json' \
  -d '{
    "guestId": "guest_device_001",
    "clientRecordId": "local_guest_001",
    "gridSize": 5,
    "ageGroup": "ADULT",
    "trainingMode": "STANDARD",
    "elapsedTimeMillis": 20100,
    "errorCount": 2,
    "scoreLevel": "NORMAL"
  }'
```

游客记录的 `ownerType` 为 `GUEST`，不会生成正式注册 ID，也不会参与账号数据统计。

## 创建训练记录

登录用户完整完成一局训练后，可将记录直接保存到当前账号。

```bash
curl -s -X POST http://127.0.0.1:8080/api/v1/me/training-records \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <accessToken>' \
  -d '{
    "clientRecordId": "local_001",
    "clientCreatedAt": "2026-06-06T15:30:00+08:00",
    "gridSize": 5,
    "ageGroup": "ADULT",
    "trainingMode": "STANDARD",
    "elapsedTimeMillis": 18420,
    "errorCount": 1,
    "scoreLevel": "GOOD"
  }'
```

服务端会根据同一用户、同一方格规格、同一年龄段、同一训练模式计算：

- `previousRecordId`
- `isPersonalBest`
- `improvementStatus`
- `timeDeltaMillis`
- `errorDelta`

`clientRecordId` 用于幂等去重。同一用户重复提交相同 `clientRecordId` 时，不会生成重复记录。

## 批量同步训练记录

```bash
curl -s -X POST http://127.0.0.1:8080/api/v1/me/training-records:batchCreate \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <accessToken>' \
  -d '{
    "items": [
      {
        "clientRecordId": "local_002",
        "gridSize": 5,
        "ageGroup": "ADULT",
        "trainingMode": "STANDARD",
        "elapsedTimeMillis": 17200,
        "errorCount": 0,
        "scoreLevel": "EXCELLENT"
      }
    ]
  }'
```

单次最多同步 100 条。

## 关联游客训练记录

用户登录或注册成功后，如果客户端发现本地存在游客记录，可提示用户是否关联。

```bash
curl -s -X POST http://127.0.0.1:8080/api/v1/me/training-records:associateGuest \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <accessToken>' \
  -d '{
    "guestId": "guest_device_001"
  }'
```

关联后，游客记录会归属到当前账号，并重新计算同条件下的上一条记录、个人最佳和进步状态。

## 查询游客记录摘要

```bash
curl -s 'http://127.0.0.1:8080/api/v1/guest/training-summary?guestId=guest_device_001'
```

## 查询训练记录

```bash
curl -s 'http://127.0.0.1:8080/api/v1/me/training-records?gridSize=5&trainingMode=STANDARD&page=1&pageSize=20' \
  -H 'Authorization: Bearer <accessToken>'
```

支持参数：

- `gridSize`: `3`、`4`、`5`、`7`
- `ageGroup`: `CHILD`、`TEEN`、`ADULT`、`SENIOR`
- `trainingMode`: `STANDARD`、`ASSISTED`
- `page`: 页码，默认 `1`
- `pageSize`: 每页数量，最大 `100`

## 获取训练统计摘要

```bash
curl -s 'http://127.0.0.1:8080/api/v1/me/training-summary?gridSize=5&ageGroup=ADULT&trainingMode=STANDARD' \
  -H 'Authorization: Bearer <accessToken>'
```

摘要包含总次数、最近记录、最佳记录、最近平均耗时和最近平均错误次数。

## 清空训练记录

客户端调用前必须先完成二次确认。

```bash
curl -s -X DELETE http://127.0.0.1:8080/api/v1/me/training-records \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <accessToken>' \
  -d '{
    "confirm": "CLEAR_MY_TRAINING_RECORDS"
  }'
```

## 退出登录

```bash
curl -s -X POST http://127.0.0.1:8080/api/v1/auth/logout \
  -H 'Content-Type: application/json' \
  -H 'Authorization: Bearer <accessToken>' \
  -d '{
    "refreshToken": "<refreshToken>"
  }'
```

## 线上 PK 占位

```bash
curl -s http://127.0.0.1:8080/api/v1/me/pk-status \
  -H 'Authorization: Bearer <accessToken>'
```

当前返回 `available: false`，用于前端展示「线上 PK 即将上线」。服务端不会返回虚假段位、胜率、在线人数或对手数据。
